package hometv.remote.bean;
/**
 * 网络模式管理者
 * 用来切换当前的局域网和远程网
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
  * 获取当前网络模式
  * @return
  */
 public NetProtecolInterface getCurNetMode(){
	 return curNetMode;
 }
}
