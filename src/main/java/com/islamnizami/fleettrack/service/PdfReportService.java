package com.islamnizami.fleettrack.service;

import com.islamnizami.fleettrack.model.entity.Driver;
import com.islamnizami.fleettrack.model.entity.MaintenanceRecord;
import com.islamnizami.fleettrack.model.entity.Vehicle;
import com.islamnizami.fleettrack.model.enums.VehicleStatus;
import com.islamnizami.fleettrack.repository.DriverRepository;
import com.islamnizami.fleettrack.repository.MaintenanceRecordRepository;
import com.islamnizami.fleettrack.repository.VehicleRepository;
import com.lowagie.text.Document;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class PdfReportService {

    private static final int MAX_MAINTENANCE_ROWS = 100;
    private static final DateTimeFormatter DATE_TIME =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm 'UTC'");

    private static final Font TITLE = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18);
    private static final Font SECTION = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 13);
    private static final Font HEADER = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 9, Font.NORMAL, Color.WHITE);
    private static final Font BODY = FontFactory.getFont(FontFactory.HELVETICA, 9);
    private static final Font SMALL = FontFactory.getFont(FontFactory.HELVETICA, 8, Font.NORMAL, Color.GRAY);

    private static final Color HEADER_BG = new Color(0x2F, 0x4F, 0x6F);

    private final VehicleRepository vehicleRepository;
    private final DriverRepository driverRepository;
    private final MaintenanceRecordRepository maintenanceRepository;

    @Transactional(readOnly = true)
    public byte[] generateFleetReport() {
        List<Vehicle> vehicles = vehicleRepository.findAllWithDriver();
        List<Driver> drivers = driverRepository.findAll(Sort.by("lastName", "firstName"));
        List<MaintenanceRecord> records = maintenanceRepository
                .findRecentWithVehicle(PageRequest.of(0, MAX_MAINTENANCE_ROWS));

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        Document document = new Document(PageSize.A4.rotate(), 36, 36, 36, 36);

        try {
            PdfWriter.getInstance(document, out);
            document.open();

            document.add(new Paragraph("FleetTrack - Fleet Report", TITLE));
            document.add(new Paragraph("Generated at "
                    + OffsetDateTime.now(ZoneOffset.UTC).format(DATE_TIME), SMALL));
            document.add(spacer());

            addStatusSection(document, vehicles);
            addVehicleSection(document, vehicles);
            addMaintenanceSection(document, records);
            addDriverSection(document, drivers, vehicles);
        } catch (Exception ex) {
            throw new IllegalStateException("Could not generate PDF report", ex);
        } finally {
            if (document.isOpen()) {
                document.close();
            }
        }

        return out.toByteArray();
    }

    private void addStatusSection(Document document, List<Vehicle> vehicles) throws Exception {
        document.add(new Paragraph("1. Fleet status", SECTION));
        document.add(spacer());

        Map<VehicleStatus, Integer> counts = new EnumMap<>(VehicleStatus.class);
        for (VehicleStatus status : VehicleStatus.values()) {
            counts.put(status, 0);
        }
        for (Vehicle vehicle : vehicles) {
            counts.merge(vehicle.getStatus(), 1, Integer::sum);
        }

        PdfPTable table = newTable(new float[]{3, 1});
        addHeader(table, "Status", "Vehicles");
        for (VehicleStatus status : VehicleStatus.values()) {
            addRow(table, status.name(), String.valueOf(counts.get(status)));
        }
        addRow(table, "TOTAL", String.valueOf(vehicles.size()));
        table.setHorizontalAlignment(Element.ALIGN_LEFT);
        table.setWidthPercentage(30);
        document.add(table);
        document.add(spacer());
    }

    private void addVehicleSection(Document document, List<Vehicle> vehicles) throws Exception {
        document.add(new Paragraph("2. Vehicles", SECTION));
        document.add(spacer());

        PdfPTable table = newTable(new float[]{1, 2, 2, 2, 1, 2, 3, 3});
        addHeader(table, "ID", "License plate", "Make", "Model", "Year",
                "Status", "Driver", "Last location");
        for (Vehicle v : vehicles) {
            String location = v.getLatitude() == null || v.getLongitude() == null
                    ? "-" : v.getLatitude() + ", " + v.getLongitude();
            addRow(table,
                    String.valueOf(v.getId()),
                    v.getLicensePlate(),
                    v.getMake(),
                    v.getModel(),
                    String.valueOf(v.getModelYear()),
                    v.getStatus().name(),
                    v.getDriver() == null ? "-" : fullName(v.getDriver()),
                    location);
        }
        if (vehicles.isEmpty()) {
            addEmptyRow(table, 8, "No vehicles registered");
        }
        document.add(table);
        document.add(spacer());
    }

    private void addMaintenanceSection(Document document, List<MaintenanceRecord> records) throws Exception {
        document.add(new Paragraph("3. Maintenance log (latest " + MAX_MAINTENANCE_ROWS + ")", SECTION));
        document.add(spacer());

        OffsetDateTime now = OffsetDateTime.now();
        PdfPTable table = newTable(new float[]{2, 5, 3, 3, 2});
        addHeader(table, "Vehicle", "Description", "Due", "Completed", "State");
        for (MaintenanceRecord m : records) {
            String state = m.isCompleted() ? "COMPLETED"
                    : m.getDueAt().isBefore(now) ? "OVERDUE" : "PENDING";
            addRow(table,
                    m.getVehicle().getLicensePlate(),
                    m.getDescription(),
                    format(m.getDueAt()),
                    format(m.getCompletedAt()),
                    state);
        }
        if (records.isEmpty()) {
            addEmptyRow(table, 5, "No maintenance records");
        }
        document.add(table);
        document.add(spacer());
    }

    private void addDriverSection(Document document, List<Driver> drivers,
                                  List<Vehicle> vehicles) throws Exception {
        document.add(new Paragraph("4. Driver activity", SECTION));
        document.add(spacer());

        Map<Long, List<String>> platesByDriver = new HashMap<>();
        for (Vehicle v : vehicles) {
            if (v.getDriver() != null) {
                platesByDriver
                        .computeIfAbsent(v.getDriver().getId(), k -> new ArrayList<>())
                        .add(v.getLicensePlate());
            }
        }

        PdfPTable table = newTable(new float[]{3, 4, 3, 1, 1, 4});
        addHeader(table, "Driver", "Email", "License no.", "Active", "Vehicles", "Assigned vehicles");
        for (Driver d : drivers) {
            List<String> plates = platesByDriver.getOrDefault(d.getId(), List.of());
            addRow(table,
                    fullName(d),
                    d.getEmail(),
                    d.getLicenseNumber(),
                    d.isActive() ? "yes" : "no",
                    String.valueOf(plates.size()),
                    plates.isEmpty() ? "-" : String.join(", ", plates));
        }
        if (drivers.isEmpty()) {
            addEmptyRow(table, 6, "No drivers registered");
        }
        document.add(table);
    }

    private PdfPTable newTable(float[] widths) {
        PdfPTable table = new PdfPTable(widths);
        table.setWidthPercentage(100);
        table.setHeaderRows(1);
        return table;
    }

    private void addHeader(PdfPTable table, String... labels) {
        for (String label : labels) {
            PdfPCell cell = new PdfPCell(new Phrase(label, HEADER));
            cell.setBackgroundColor(HEADER_BG);
            cell.setPadding(4);
            table.addCell(cell);
        }
    }

    private void addRow(PdfPTable table, String... values) {
        for (String value : values) {
            PdfPCell cell = new PdfPCell(new Phrase(value == null ? "-" : value, BODY));
            cell.setPadding(3);
            table.addCell(cell);
        }
    }

    private void addEmptyRow(PdfPTable table, int columns, String text) {
        PdfPCell cell = new PdfPCell(new Phrase(text, SMALL));
        cell.setColspan(columns);
        cell.setPadding(4);
        table.addCell(cell);
    }

    private Paragraph spacer() {
        return new Paragraph(" ", SMALL);
    }

    private String fullName(Driver driver) {
        return driver.getFirstName() + " " + driver.getLastName();
    }

    private String format(OffsetDateTime time) {
        return time == null ? "-" : time.withOffsetSameInstant(ZoneOffset.UTC).format(DATE_TIME);
    }
}
