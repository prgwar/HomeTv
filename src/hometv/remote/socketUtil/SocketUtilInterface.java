package hometv.remote.socketUtil;
/**
 * no use this class 
 *
 */
public interface SocketUtilInterface {
	/**
	 * 初始化控制协议,连接前端服务器
	 * @param serverIP
	 * @param port
	 * @return 成功返回0；失败则返回-1
	 * note 如果psRect为SDI_NULL，则表示全屏显示
	 */
	public int initControlServer(String serverIP,int port);
	/**
	 * 反初始化控制协议,释放相关资源
	 * @return
	 */
	public int deinitControlServer();
	/**
	 * 登录前端服务器 (实际上是匹配对应的远程BOX信息是否正确)
	 * @param userName
	 * @param passWord
	 * @return 成功返回0；失败则返回-1
	 */
	public int logonServer(String userName,String passWord);
	/**
	 * 获取播放的URL地址
	 * @param url  url就是服务器返回可以播放的数据URL
	 * @return 成功返回0；失败则返回-1
	 */
	public int getPlayUrl();
	/**
	 * 发送网络数据给服务器,根据定义的私有控制协议组包即可
	 * @param netData 发送控制数据到前端服务器
	 * @return 成功返回0；失败则返回-1
	 */
	public int sendControlDataToServer(String netData);
	/**
	 * 解析服务器发送给设备终端的网络数据,根据定义的私有控制协议解析即可，
		主要是服务器状态反馈信息

	 * @param fromServerData 从前端服务器发过来的TCP网络控制协议数据
	 * @return 成功返回0；失败则返回-1
	 * note 该接口应定义为私有接口，在接受网络数据线程中调用即可。
	 */
	public int parseControlDataFromServer(String fromServerData);
	public int play(byte startPVR);
	public void stop();
	public void irControl(byte irCode);
}
