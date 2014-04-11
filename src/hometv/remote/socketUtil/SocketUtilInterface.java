package hometv.remote.socketUtil;
/**
 * no use this class 
 *
 */
public interface SocketUtilInterface {
	/**
	 * ��ʼ������Э��,����ǰ�˷�����
	 * @param serverIP
	 * @param port
	 * @return �ɹ�����0��ʧ���򷵻�-1
	 * note ���psRectΪSDI_NULL�����ʾȫ����ʾ
	 */
	public int initControlServer(String serverIP,int port);
	/**
	 * ����ʼ������Э��,�ͷ������Դ
	 * @return
	 */
	public int deinitControlServer();
	/**
	 * ��¼ǰ�˷����� (ʵ������ƥ���Ӧ��Զ��BOX��Ϣ�Ƿ���ȷ)
	 * @param userName
	 * @param passWord
	 * @return �ɹ�����0��ʧ���򷵻�-1
	 */
	public int logonServer(String userName,String passWord);
	/**
	 * ��ȡ���ŵ�URL��ַ
	 * @param url  url���Ƿ��������ؿ��Բ��ŵ�����URL
	 * @return �ɹ�����0��ʧ���򷵻�-1
	 */
	public int getPlayUrl();
	/**
	 * �����������ݸ�������,���ݶ����˽�п���Э���������
	 * @param netData ���Ϳ������ݵ�ǰ�˷�����
	 * @return �ɹ�����0��ʧ���򷵻�-1
	 */
	public int sendControlDataToServer(String netData);
	/**
	 * �������������͸��豸�ն˵���������,���ݶ����˽�п���Э��������ɣ�
		��Ҫ�Ƿ�����״̬������Ϣ

	 * @param fromServerData ��ǰ�˷�������������TCP�������Э������
	 * @return �ɹ�����0��ʧ���򷵻�-1
	 * note �ýӿ�Ӧ����Ϊ˽�нӿڣ��ڽ������������߳��е��ü��ɡ�
	 */
	public int parseControlDataFromServer(String fromServerData);
	public int play(byte startPVR);
	public void stop();
	public void irControl(byte irCode);
}
