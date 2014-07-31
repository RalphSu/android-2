package test;

import com.ice.jni.registry.*;
import java.text.SimpleDateFormat;

/** */
/**
 * java ����ע���
 * 
 * @author ��־Զ
 */
public class RegeditTool {

	static SimpleDateFormat shortDateFormat = new SimpleDateFormat("yyyy-MM-dd");

	/** */
	/** */
	/** */
	/** Creates a new instance of test */

	// ����Ϣ�洢��ע���HKEY_LOCAL_MACHINE�µ�ĳ���ڵ��ĳһ�����У������޸ģ����򴴽�
	public static boolean setValue(String folder, String subKeyNode, String subKeyName, String subKeyValue) {
		try {
			RegistryKey software = Registry.HKEY_LOCAL_MACHINE.openSubKey(folder);
			RegistryKey subKey = software.createSubKey(subKeyNode, "");
			subKey.setValue(new RegStringValue(subKey, subKeyName, subKeyValue));
			subKey.closeKey();
			return true;
		} catch (NoSuchKeyException e) {
			e.printStackTrace();
		} catch (NoSuchValueException e) {
			e.printStackTrace();
		} catch (RegistryException e) {
			e.printStackTrace();
		}
		return false;
	}

	// ɾ��ע�����ĳ�ڵ��µ�ĳ������
	public static boolean deleteValue(String folder, String subKeyNode, String subKeyName) {

		try {
			RegistryKey software = Registry.HKEY_LOCAL_MACHINE.openSubKey(folder);
			RegistryKey subKey = software.createSubKey(subKeyNode, "");
			subKey.deleteValue(subKeyName);
			subKey.closeKey();
			return true;
		} catch (NoSuchKeyException e) {
			System.out.println("NOsuchKey_delete");
		} catch (NoSuchValueException e) {
			System.out.println("NOsuchValue_delete");
		} catch (RegistryException e) {
			e.printStackTrace();
		}
		return false;
	}

	// ɾ��ע�����ĳ�ڵ��µ�ĳ�ڵ�
	public static boolean deleteSubKey(String folder, String subKeyNode) {
		try {
			RegistryKey software = Registry.HKEY_LOCAL_MACHINE.openSubKey(folder);
			software.deleteSubKey(subKeyNode);
			software.closeKey();
			return true;
		} catch (NoSuchKeyException e) {
			e.printStackTrace();
		} catch (RegistryException e) {
			e.printStackTrace();
		}
		return false;
	}

	// ��ע����������Ӧ�ı�������ֵ
	public static String getValue(String folder, String subKeyNode, String subKeyName) {
		String value = "";
		try {
			RegistryKey software = Registry.HKEY_LOCAL_MACHINE.openSubKey(folder);
			RegistryKey subKey = software.openSubKey(subKeyNode);
			value = subKey.getStringValue(subKeyName);
			subKey.closeKey();
		} catch (NoSuchKeyException e) {
			value = "NoSuchKey";
			// e.printStackTrace();
		} catch (NoSuchValueException e) {
			value = "NoSuchValue";
			// e.printStackTrace();
		} catch (RegistryException e) {
			e.printStackTrace();
		}
		return value;
	}

	// ����
	public static void main(String[] args) {
		setValue("SOFTWARE", "Microsoft\\Windows\\CurrentVersion\\Run", "test", "C:\\1.exe");
	}
}