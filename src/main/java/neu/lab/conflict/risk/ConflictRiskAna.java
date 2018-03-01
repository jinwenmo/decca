package neu.lab.conflict.risk;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import neu.lab.conflict.Conf;
import neu.lab.conflict.container.FinalClasses;
import neu.lab.conflict.util.MavenUtil;
import neu.lab.conflict.util.SootUtil;
import neu.lab.conflict.vo.ClassVO;
import neu.lab.conflict.vo.DepJar;
import neu.lab.conflict.vo.NodeConflict;

public class ConflictRiskAna {
	private List<JarRiskAna> jarRiskAnas;
	private NodeConflict nodeConflict;

	private Set<String> rchedMthds;// reached method in call-graph computed(entry class is host class)
	private Set<String> rchedMthdNames;

	private Set<String> rchedServices;
	private Set<String> rchedServiceNames;

	private Set<String> risk1Mthds;// reached and thrown
	private Set<String> risk2Mthds;// reached and thrown and called by method in other jar.

	private ConflictRiskAna(NodeConflict nodeConflict) {
		this.nodeConflict = nodeConflict;
	}

	public List<JarRiskAna> getJarRiskAnas() {
		return jarRiskAnas;
	}

	private void setJarRiskAnas(List<JarRiskAna> jarRiskAnas) {
		this.jarRiskAnas = jarRiskAnas;
	}

	public String getRiskString() {
		StringBuilder sb = new StringBuilder("risk for conflict:");
		sb.append(nodeConflict.toString() + "\n");
		sb.append("reached size: " + getRchedMthds().size() + " reached_thrown size:" + getRisk1Mthds().size()
				+ " reached_thrown_service:" + getRisk2Mthds().size() + "\n");
		for (JarRiskAna jarRiskAna : getJarRiskAnas()) {
			sb.append(jarRiskAna.getRiskString());
			sb.append("\n");
		}
		return sb.toString();
	}

	public FourRow getFourRow() {
		List<String> mthdRow = getRecord("mthd");
		List<String> mthdNameRow = getRecord("mthdName");
		List<String> serviceRow = getRecord("service");
		List<String> serviceNameRow = getRecord("serviceName");
		// add origin column
		mthdRow.add("" + this.getRchedMthds().size());
		mthdNameRow.add("" + this.getRchedMthdNames().size());
		serviceRow.add("" + this.getRchedServices().size());
		serviceNameRow.add("" + this.getRchedServiceNames().size());
		// add load column
		FourNum loadFourNum = getJarFourNum(nodeConflict.getUsedDepJar());
		mthdRow.add("" + loadFourNum.mthdCnt);
		mthdNameRow.add("" + loadFourNum.mthdNameCnt);
		serviceRow.add("" + loadFourNum.serviceCnt);
		serviceNameRow.add("" + loadFourNum.serviceNameCnt);
		// add other column
		String otherMthd = "";
		String otherMthdName = "";
		String otherService = "";
		String otherServiceName = "";
		for (DepJar depJar : nodeConflict.getDepJars()) {
			if (nodeConflict.getUsedDepJar() != depJar) {
				FourNum fourNum = getJarFourNum(depJar);
				otherMthd = otherMthd + "/" + fourNum.mthdCnt;
				otherMthdName = otherMthdName + "/" + fourNum.mthdNameCnt;
				otherService = otherService + "/" + fourNum.serviceCnt;
				otherServiceName = otherServiceName + "/" + fourNum.serviceNameCnt;
			}
		}
		mthdRow.add(otherMthd);
		mthdNameRow.add(otherMthdName);
		serviceRow.add(otherService);
		serviceNameRow.add(otherServiceName);
		return new FourRow(mthdRow, mthdNameRow, serviceRow, serviceNameRow);
	}

	private FourNum getJarFourNum(DepJar jar) {
		Set<String> jarMthdSigs = jar.getAllMthd();
		Set<String> jarMthdNames = new HashSet<String>();
		for (String methodSig : jarMthdSigs) {
			jarMthdNames.add(SootUtil.mthdSig2Name(methodSig));
		}

		FourNum fourNum = new FourNum();
		for (String rchMthd : this.getRchedMthds()) {
			if (jarMthdSigs.contains(rchMthd))
				fourNum.mthdCnt++;
		}
		for (String rchMthdName : this.getRchedMthdNames()) {
			if (jarMthdNames.contains(rchMthdName))
				fourNum.mthdNameCnt++;
		}
		for (String rchService : this.getRchedServices()) {
			if (jarMthdSigs.contains(rchService))
				fourNum.serviceCnt++;
		}
		for (String rchServiceName : this.getRchedServiceNames()) {
			if (jarMthdNames.contains(rchServiceName))
				fourNum.serviceNameCnt++;
		}
		return fourNum;
	}

	private List<String> getRecord(String staType) {
		List<String> record = new ArrayList<String>();
		record.add(MavenUtil.i().getProjectGroupId() + ":" + MavenUtil.i().getProjectArtifactId() + ":"
				+ MavenUtil.i().getProjectVersion());
		record.add(nodeConflict.getGroupId() + ":" + nodeConflict.getArtifactId());
		record.add(staType);
		return record;
	}

	public static ConflictRiskAna getConflictRiskAna(NodeConflict nodeConflict) {
		MavenUtil.i().getLog().info("risk ana for:" + nodeConflict.toString());
		ConflictRiskAna riskAna = new ConflictRiskAna(nodeConflict);
		List<JarRiskAna> jarRiskAnas = new ArrayList<JarRiskAna>();
		for (DepJar depJar : nodeConflict.getDepJars()) {
			jarRiskAnas.add(depJar.getJarRiskAna(getClsTb(nodeConflict)));
		}
		riskAna.setJarRiskAnas(jarRiskAnas);
		return riskAna;
	}

	private static Map<String, ClassVO> getClsTb(NodeConflict nodeConflict) {
		if (Conf.CLASS_DUP)
			return FinalClasses.i().getClsTb();
		else
			return nodeConflict.getUsedDepJar().getClsTb();
	}

	public Set<String> getRchedMthds() {
		if (rchedMthds == null) {
			rchedMthds = new HashSet<String>();
			for (JarRiskAna jarRiskAna : getJarRiskAnas()) {
				rchedMthds.addAll(jarRiskAna.getRchedMthds());
			}
		}
		return rchedMthds;
	}

	public Set<String> getRchedServices() {
		if (rchedServices == null) {
			rchedServices = new HashSet<String>();
			for (JarRiskAna jarRiskAna : getJarRiskAnas()) {
				rchedServices.addAll(jarRiskAna.getRchedServices());
			}
		}
		return rchedServices;
	}

	public Set<String> getRisk1Mthds() {
		if (risk1Mthds == null) {
			risk1Mthds = new HashSet<String>();
		}
		return risk1Mthds;
	}

	public Set<String> getRisk2Mthds() {
		if (risk2Mthds == null) {
			risk2Mthds = new HashSet<String>();
		}
		return risk2Mthds;
	}

	public Set<String> getRchedMthdNames() {
		if (null == rchedMthdNames) {
			rchedMthdNames = new HashSet<String>();
			for (String methodSig : getRchedMthds()) {
				rchedMthdNames.add(SootUtil.mthdSig2Name(methodSig));
			}
		}
		return rchedMthdNames;
	}

	public Set<String> getRchedServiceNames() {
		if (null == rchedServiceNames) {
			rchedServiceNames = new HashSet<String>();
			for (String methodSig : getRchedServices()) {
				rchedServiceNames.add(SootUtil.mthdSig2Name(methodSig));
			}
		}
		return rchedServiceNames;
	}

}
