package neu.lab.conflict;

import java.io.File;

import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

import neu.lab.conflict.container.DepJars;
import neu.lab.conflict.container.FinalClasses;
import neu.lab.conflict.soot.JarAna;
import neu.lab.conflict.soot.SootCg;
import neu.lab.conflict.util.MavenUtil;
import neu.lab.conflict.util.UserConf;
import neu.lab.conflict.writer.JarRchedWriter;

/**
 *
 */
@Mojo(name = "detect", defaultPhase = LifecyclePhase.PRE_INTEGRATION_TEST)
public class DetectMojo extends ConflictMojo {

	@Parameter(property = "resultFileType", defaultValue = "xml")
	protected String resultFileType;

	@Parameter(property = "resultFilePath")
	protected String resultFilePath;

	@Override
	public void run() {
		// new RiskPathWriter().write(Conf.outDir + "detect.txt");

		if (Conf.ANA_FROM_HOST && !MavenUtil.i().getBuildDir().exists()) {
			getLog().warn(MavenUtil.i().getProjectInfo() + " dont't have target! skip");
		} else {
			if (Conf.CLASS_DUP)
				FinalClasses.init(DepJars.i());
			if ("xml".equals(resultFileType)) {
				if (null == resultFilePath) {
					resultFilePath = MavenUtil.i().getBuildDir().getAbsolutePath() + File.separator
							+ "levelPredict.xml";
				}
				new JarRchedWriter().writeDom(resultFilePath, append);
			} else if ("csv".equals(resultFileType)) {
				if (null == resultFilePath) {
					resultFilePath = MavenUtil.i().getBuildDir().getAbsolutePath() + File.separator + "reachnum.csv";
				}
				new JarRchedWriter().writeCsv(resultFilePath, append);
			} else {
				getLog().error("resultFileType can be xml/csv , can't be " + resultFileType);
			}
			getLog().info("jarDeconstrction time:" + JarAna.runtime);
			getLog().info("call graph time:" + SootCg.runtime);
		}

	}

}
