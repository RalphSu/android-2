package test;
import com.ice.jni.registry.NoSuchKeyException;
import com.ice.jni.registry.RegStringValue;
import com.ice.jni.registry.Registry;
import com.ice.jni.registry.RegistryException;
import com.ice.jni.registry.RegistryKey;

/**
 * @author solo L
 *
 */
public class JNIRegistryTest {
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// 创建注册表项并设置相应的值
		try {
			System.out.println("start");
			RegistryKey software = Registry.HKEY_LOCAL_MACHINE.openSubKey("SOFTWARE");
			RegistryKey subKey = software.createSubKey("SubKeyName", "");
			subKey.setValue(new RegStringValue(subKey, "subKey1", "subKey1Value"));
			subKey.setValue(new RegStringValue(subKey, "subKey2", "subKey2Value"));
			subKey.closeKey();
		} catch (NoSuchKeyException e) {
			e.printStackTrace();
		} catch (RegistryException e) {
			e.printStackTrace();
		}

		// 打开注册表项并读出相应的值
		try {
			RegistryKey software = Registry.HKEY_LOCAL_MACHINE.openSubKey("SOFTWARE");
			RegistryKey subKey = software.openSubKey("SubKeyName");
			String subKey1Value = subKey.getStringValue("subKey1");
			String subKey2Value = subKey.getStringValue("subKey2");
			System.out.println(subKey1Value);
			System.out.println(subKey2Value);
			subKey.closeKey();
		} catch (NoSuchKeyException e) {
			e.printStackTrace();
		} catch (RegistryException e) {
			e.printStackTrace();
		}
	}
}