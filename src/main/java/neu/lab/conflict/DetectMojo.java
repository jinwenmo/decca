package neu.lab.conflict;

import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;

import neu.lab.conflict.container.DepJars;
import neu.lab.conflict.container.FinalClasses;
import neu.lab.conflict.container.NodeConflicts;
import neu.lab.conflict.soot.JarAna;
import neu.lab.conflict.soot.SootCg;
import neu.lab.conflict.util.MavenUtil;
import neu.lab.conflict.vo.NodeConflict;
import neu.lab.conflict.writer.ReachNumWriter;
import neu.lab.conflict.writer.RiskPathWriter;

/**
 *
 */
@Mojo(name = "detect", defaultPhase = LifecyclePhase.PRE_INTEGRATION_TEST)
public class DetectMojo extends ConflictMojo {

	@Override
	public void run() {

		// new RiskPathWriter().write(Conf.outDir + "detect.txt");
		if (Conf.ANA_FROM_HOST&&!MavenUtil.i().getBuildDir().exists()) {
			getLog().warn(MavenUtil.i().getProjectInfo()+" dont't have target! skip");
		}else {
			if (Conf.CLASS_DUP)
				FinalClasses.init(DepJars.i());
			new ReachNumWriter().write(Conf.outDir + "reachnum.csv");

			getLog().info("jarDeconstrction time:" + JarAna.runtime);
			getLog().info("call graph time:" + SootCg.runtime);
		}

	}

}
