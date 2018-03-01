package neu.lab.conflict;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;

import neu.lab.conflict.container.DepJars;
import neu.lab.conflict.container.NodeAdapters;
import neu.lab.conflict.util.MavenUtil;
import neu.lab.conflict.vo.NodeAdapter;

@Mojo(name = "debug", defaultPhase = LifecyclePhase.VALIDATE)
public class DebugMojo extends ConflictMojo {

	@Override
	public void run() {
		// Cmp.cmp();
		write(Conf.outDir + "debug.csv");

	}

	public void write(String outPath) {
		try {
			CSVPrinter printer = new CSVPrinter(new FileWriter(outPath, true), CSVFormat.DEFAULT);
			List<String> record = new ArrayList<String>();
			int directDep = 0;
			int allNode = -1 + NodeAdapters.i().getAllNodeAdapter().size();
			int allJar = -1 + DepJars.i().getAllDepJar().size();
			int allUsedJar = -1;
			for (NodeAdapter node : NodeAdapters.i().getAllNodeAdapter()) {
				if (node.getNodeDepth() == 2) {
					directDep++;
				}
				if (node.isNodeSelected()) {
					allUsedJar++;
				}
			}
			record.add(MavenUtil.i().getProjectInfo());
			record.add("" + directDep);
			record.add("" + allNode);
			record.add("" + allJar);
			record.add("" + allUsedJar);
			printer.printRecord(record);
			printer.close();
		} catch (Exception e) {
			MavenUtil.i().getLog().error("can't write debug:", e);
		}
	}
}
