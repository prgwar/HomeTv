package hometv.remote.bean;
/**
 * ����ģʽ������
 * �����л���ǰ�ľ�������Զ����
 * @author fee
 *
 */
public class NetModeManager {
 private NetProtecolInterface curNetMode =null ;
 private NetModeManager(){}
 public static NetModeManager netModeManager;
 public static NetModeManager getNetModeManager(){
	 if(netModeManager == null){
		 netModeManager = new NetModeManager();
	 }
	 return netModeManager;
 }
 public void switchCurNetMode(NetProtecolInterface curNet){
		 this.curNetMode = curNet;
 }
 /**
  * ��ȡ��ǰ����ģʽ
  * @return
  */
 public NetProtecolInterface getCurNetMode(){
	 return curNetMode;
 }
}
