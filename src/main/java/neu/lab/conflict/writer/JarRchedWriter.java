package neu.lab.conflict.writer;

import java.io.FileWriter;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;

import neu.lab.conflict.container.NodeConflicts;
import neu.lab.conflict.risk.FourRow;
import neu.lab.conflict.util.MavenUtil;
import neu.lab.conflict.vo.NodeConflict;

public class JarRchedWriter {
	public void write(String outPath) {
		try {
//			final String[] header = { "projectId", "conflictId", "type", "origin", "load", "other" };
//			CSVFormat format = CSVFormat.DEFAULT.withHeader(header);
			CSVPrinter printer = new CSVPrinter(new FileWriter(outPath, true), CSVFormat.DEFAULT);
			for (NodeConflict conflict : NodeConflicts.i().getConflicts()) {
				FourRow fourRow = conflict.getRiskAna().getFourRow();
				printer.printRecord(fourRow.mthdRow);
				printer.printRecord(fourRow.mthdNameRow);
				printer.printRecord(fourRow.serviceRow);
				printer.printRecord(fourRow.serviceNameRow);
				printer.flush();
			}
			printer.close();
		} catch (Exception e) {
			MavenUtil.i().getLog().error("can't write reach number:", e);
		}
	}
}
