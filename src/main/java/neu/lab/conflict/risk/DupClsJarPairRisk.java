package neu.lab.conflict.risk;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.dom4j.Element;
import org.dom4j.tree.DefaultElement;

import neu.lab.conflict.statics.DupClsJarPair;
import neu.lab.conflict.util.MavenUtil;
import neu.lab.conflict.util.SootUtil;
import neu.lab.conflict.vo.DepJar;

public class DupClsJarPairRisk {
	private DupClsJarPair jarPair;

	private Set<String> rchedMthds;
	private Set<String> rchedMthdNames;

	private Set<String> rchedServices;
	private Set<String> rchedServiceNames;

	public DupClsJarPairRisk(DupClsJarPair jarPair, DepJarCg cg1, DepJarCg cg2) {
		this.jarPair = jarPair;
		this.rchedMthds = new HashSet<String>();
		this.rchedMthds = new HashSet<String>();
		this.addRched(cg1);
		this.addRched(cg2);
	}

//	public Element getConflictElement() {
//	Element conflictEle = new DefaultElement("conflict");
//	
//}
	
	private void addRched(DepJarCg cg) {
		for (String rchedMthd : cg.getRchedMthds()) {
			if (jarPair.isInDupCls(rchedMthd))
				rchedMthds.add(rchedMthd);
		}
		for (String rchedService : cg.getRchedServices()) {
			if (jarPair.isInDupCls(rchedService))
				rchedServices.add(rchedService);
		}
	}

	public Set<String> getRchedServiceNames() {
		if (null == rchedServiceNames) {
			rchedServiceNames = new HashSet<String>();
			for (String methodSig : getRchedServices()) {
				rchedServiceNames.add(SootUtil.mthdSig2name(methodSig));
			}
		}
		return rchedServiceNames;
	}

	public Set<String> getRchedMthdNames() {
		if (null == rchedMthdNames) {
			rchedMthdNames = new HashSet<String>();
			for (String methodSig : getRchedMthds()) {
				rchedMthdNames.add(SootUtil.mthdSig2name(methodSig));
			}
		}
		return rchedMthdNames;
	}

	public Set<String> getRchedMthds() {
		return rchedMthds;
	}

	public Set<String> getRchedServices() {
		return rchedServices;
	}

	public FourRow getFourRow() {
		List<String> mthdRow = getRecord("mthd");
		List<String> mthdNameRow = getRecord("mthdName");
		List<String> serviceRow = getRecord("service");
		List<String> serviceNameRow = getRecord("serviceName");
		//add origin column
		mthdRow.add("" + this.getRchedMthds().size());
		mthdNameRow.add("" + this.getRchedMthdNames().size());
		serviceRow.add("" + this.getRchedServices().size());
		serviceNameRow.add("" + this.getRchedServiceNames().size());
		//add jar1 column
		FourNum jar1FourNum = getJarFourNum(jarPair.getJar1());
		mthdRow.add("" + jar1FourNum.mthdCnt);
		mthdNameRow.add("" + jar1FourNum.mthdNameCnt);
		serviceRow.add("" + jar1FourNum.serviceCnt);
		serviceNameRow.add("" + jar1FourNum.serviceNameCnt);
		//add jar2 column
		FourNum jar2FourNum = getJarFourNum(jarPair.getJar2());
		mthdRow.add("" + jar2FourNum.mthdCnt);
		mthdNameRow.add("" + jar2FourNum.mthdNameCnt);
		serviceRow.add("" + jar2FourNum.serviceCnt);
		serviceNameRow.add("" + jar2FourNum.serviceNameCnt);
		return new FourRow(mthdRow, mthdNameRow, serviceRow, serviceNameRow);
	}

	private FourNum getJarFourNum(DepJar jar) {
		Set<String> jarMthdSigs = jar.getAllMthd();
		Set<String> jarMthdNames = new HashSet<String>();
		for (String methodSig : jarMthdSigs) {
			jarMthdNames.add(SootUtil.mthdSig2name(methodSig));
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
	public List<String> getRecord(String type){
		List<String> record = new ArrayList<String>();
		record.add(MavenUtil.i().getProjectGroupId() + ":" + MavenUtil.i().getProjectArtifactId() + ":"
				+ MavenUtil.i().getProjectVersion());
		record.add(jarPair.getJar1().toString());
		record.add(jarPair.getJar2().toString());
		return record;
	}

}
