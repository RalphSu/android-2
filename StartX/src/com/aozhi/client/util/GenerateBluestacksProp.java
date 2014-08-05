package com.aozhi.client.util;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class GenerateBluestacksProp {

	public static String generate(String[] parms) {
		String productName = "华为-B199";
		String manufacturerName = "华为";
		if (parms.length > 1) {
			productName = parms[0];
			manufacturerName = parms[1];
		}

		StringBuffer buffer = new StringBuffer();
		buffer.append("# begin build properties").append("\r\n");
		buffer.append("# autogenerated by buildinfo.sh").append("\r\n");
		buffer.append("ro.build.id=GRJ22").append("\r\n");
		buffer.append("ro.build.display.id=").append(productName)
				.append("-user 2.3.4 GRJ22 eng.build.20120314.185218 release-keys").append("\r\n");
		buffer.append("ro.build.version.incremental=eng.build.20120314.185218").append("\r\n");
		buffer.append("ro.build.version.sdk=10").append("\r\n");
		buffer.append("ro.build.version.codename=REL").append("\r\n");
		// ro.build.version.release=4.0.2
		buffer.append("ro.build.version.release=2.3.4").append("\r\n");
		buffer.append("ro.build.date=Wed Mar 14 20:27:49 IST 2012").append("\r\n");
		buffer.append("ro.build.date.utc=1331737069").append("\r\n");
		buffer.append("ro.build.type=eng").append("\r\n");
		buffer.append("ro.build.user=build").append("\r\n");
		buffer.append("ro.build.host=BuildServer").append("\r\n");
		buffer.append("ro.build.tags=release-keys").append("\r\n");
		// ro.product.model
		buffer.append("ro.product.model=").append(productName).append("\r\n");
		buffer.append("ro.product.brand=").append(manufacturerName).append("\r\n");
		// ro.product.name
		buffer.append("ro.product.name=").append(productName).append("\r\n");
		// ro.product.device
		buffer.append("ro.product.device=").append(productName).append("\r\n");
		buffer.append("ro.product.board=").append(productName).append("\r\n");
		buffer.append("ro.product.cpu.abi=armeabi-v7a").append("\r\n");
		buffer.append("ro.product.cpu.abi2=armeabi").append("\r\n");
		// ro.product.manufacturer
		buffer.append("ro.product.manufacturer=").append(manufacturerName).append("\r\n");
		buffer.append("ro.product.locale.language=en").append("\r\n");
		buffer.append("ro.product.locale.region=US").append("\r\n");
		buffer.append("ro.wifi.channels=").append("\r\n");
		buffer.append("ro.board.platform=").append("\r\n");
		buffer.append("# ro.build.product is obsolete; use ro.product.device").append("\r\n");
		// ro.build.product
		buffer.append("ro.build.product=").append(productName).append("\r\n");
		// ro.build.description
		buffer.append("# Do not try to parse ro.build.description or .fingerprint").append("\r\n");
		buffer.append("ro.build.description=").append(productName)
				.append("-user 2.3.4 GRJ22 eng.build.20120314.185218 release-keys").append("\r\n");
		// ro.build.fingerprint=samsung/yakjusc/maguro:4.0.2/ICL53F/SC04DOMLA1:user/release-keys
		buffer.append("ro.build.fingerprint=samsung/").append(productName).append("/").append(productName)
				.append(":2.3.4/GRJ22/eng.build.20120314.185218:eng/release-keys").append("\r\n");
		buffer.append("# end build properties").append("\r\n");
		buffer.append("").append("\r\n");
		buffer.append("#").append("\r\n");
		buffer.append("#Hardware properties").append("\r\n");
		buffer.append("#").append("\r\n");
		buffer.append("ro.hardware=smdkc210").append("\r\n");
		buffer.append("ro.bootmode=unknown").append("\r\n");
		buffer.append("ro.baseband=unknown").append("\r\n");
		buffer.append("ro.carrier=unknown").append("\r\n");
		buffer.append("ro.bootloader=unknown").append("\r\n");
		buffer.append("ro.revision=8").append("\r\n");
		buffer.append("").append("\r\n");
		buffer.append("#").append("\r\n");
		buffer.append("# ADDITIONAL_BUILD_PROPERTIES").append("\r\n");
		buffer.append("#").append("\r\n");
		buffer.append("ro.config.notification_sound=OnTheHunt.ogg").append("\r\n");
		buffer.append("ro.config.alarm_alert=Alarm_Classic.ogg").append("\r\n");
		buffer.append("ro.com.android.dataroaming=true").append("\r\n");
		buffer.append("ro.kernel.android.checkjni=1").append("\r\n");
		buffer.append("ro.setupwizard.mode=OPTIONAL").append("\r\n");
		buffer.append("net.bt.name=Android").append("\r\n");
		buffer.append("dalvik.vm.stack-trace-file=/data/anr/traces.txt").append("\r\n");
		buffer.append("").append("\r\n");
		buffer.append("#").append("\r\n");
		buffer.append("# Misc Properties").append("\r\n");
		buffer.append("#").append("\r\n");
		buffer.append("ro.opengles.version=131072").append("\r\n");
		buffer.append("ro.com.gmsversion=2.3_r3").append("\r\n");
		buffer.append("ro.com.google.clientidbase=android-samsung").append("\r\n");
		buffer.append("ro.com.google.locationfeatures=1").append("\r\n");
		buffer.append("gsm.version.ril-impl=\"Samsung RIL(IPC) v1.0\"").append("\r\n");
		buffer.append("gsm.sim.operator.numeric=310260").append("\r\n");
		buffer.append("gsm.sim.operator.alpha=T-Mobile").append("\r\n");
		buffer.append("gsm.sim.operator.iso-country=us").append("\r\n");
		buffer.append("gsm.sim.state=READY").append("\r\n");
		buffer.append("gsm.current.phone-type=1").append("\r\n");
		buffer.append("gsm.operator.alpha=T-Mobile").append("\r\n");
		buffer.append("gsm.operator.numeric=310260").append("\r\n");
		buffer.append("gsm.operator.iso-country=us").append("\r\n");
		buffer.append("gsm.operator.isroaming=false").append("\r\n");
		buffer.append("gsm.network.type=UMTS").append("\r\n");
		return buffer.toString();
	}

	/**
	 * 生成文件bulestacks.prop
	 * 
	 * @param filename
	 * @param content
	 */
	private void generateFile(String content) {
		try {
			File file = new File("bulestacks.prop");
			if (!file.exists()) {
				file.createNewFile();
			}
			FileWriter fw = new FileWriter(file.getAbsoluteFile());
			BufferedWriter bw = new BufferedWriter(fw);
			bw.write(content);
			bw.close();

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 生成bluestacks.prop文件
	 * 
	 * @param parms
	 *            参数
	 */
	public void generateBluestackProp(String[] parms) {
		String content = generate(parms);
		generateFile(content);
	}
	public static void main(String[] args) {
		GenerateBluestacksProp cBluestacksProp=new GenerateBluestacksProp();
		cBluestacksProp.generateBluestackProp(args);
	}
}
