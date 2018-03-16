package neu.lab.conflict.writer;

import java.io.FileWriter;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;

import neu.lab.conflict.container.NodeConflicts;
import neu.lab.conflict.risk.FourRow;
import neu.lab.conflict.util.MavenUtil;
import neu.lab.conflict.vo.NodeConflict;

public class JarRchedWriter {
	public void writeCsv(String outPath,boolean append) {
		try {
//			final String[] header = { "projectId", "conflictId", "type", "origin", "load", "other" };
//			CSVFormat format = CSVFormat.DEFAULT.withHeader(header);
			
			CSVPrinter printer = new CSVPrinter(new FileWriter(outPath, append), CSVFormat.DEFAULT);
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
			MavenUtil.i().getLog().error("can't write risk result:", e);
		}
	}
	
	public void writeDom(String outPath,boolean append) {
		try {
			FileWriter fileWriter = new FileWriter(outPath,append);
			OutputFormat format = OutputFormat.createPrettyPrint();
			XMLWriter writer = new XMLWriter(fileWriter, format);
			Document document = DocumentHelper.createDocument();
			Element root = document.addElement("project");
			root.addAttribute("projectInfo", MavenUtil.i().getProjectInfo());
			Element jarConfs = root.addElement("conflicts");
			jarConfs.addAttribute("type", "jar");
			for (NodeConflict conflict : NodeConflicts.i().getConflicts()) {
				jarConfs.add(conflict.getRiskAna().getConflictElement());
			}
			writer.write(document);
			writer.close();
		} catch (Exception e) {
			MavenUtil.i().getLog().error("can't write risk result:", e);
		}
	}

	public void writeAll(String string, boolean append) {
		// TODO Auto-generated method stub
		
	}
}
