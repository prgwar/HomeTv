package hometv.remote.socketUtil;

public class Constant {
	public static boolean debugflag=true;
	public static final int HOST_ERROR=1;
	public static final int CONNECT_ERROR=2;
	public static final int CONNECT_SUCCESS=3;
	public static final int CONNECT_BREAK=4;
	public static final int CONNECT_REALY_DISCONNECT=5;
	public static final String PREFERENCEINFO="hometv_setting";
	public static final String SERVERIP_KEY="serverIP";
	public static final String USERNAME_KEY="username";
	public static final String PW_KEY="password";
	public static final String REMOTE_PLAY_ALLOW_STATE="allow_remote_plaly";
	public static final String PVR_ALLOW_STATE="allow_local_pvr";
	public static final String UDPCALLBACK_EVENT_TYPE="udp_event_type";
	public static final String UDPCALLBACK_EVENT_MESSAGE="udp_event_message";
	
	//**********************Home TV Settings Keys**************
	public static final String ENABLE_REMOTE="is_remote_enable";
	public static final String ENABLE_LOCAL="is_local_enable";
	public static final String ENABLE_SENSOR_SPEED="is_sensor_speed_enable";
	public static final String ENABLE_SENSOR_DIRECTION="is_sensor_direction_enable";
	public static final String ENABLE_HW_DECODEING="is_hw_decoding_enable";
	public static final String CUR_SELECTED_PIC_QUAL_INDEX="cur_select_pic_index";
	
	public static final String PROGRAM_TV_KEY ="tvprogram";
	public static final String PROGRAM_RADIO_KEY ="radioprogram";
	public static final String PROGRAM_FAVORATE_KEY ="favprogram";
	public static final String PROGRAM_ALL ="allprogram";
	public static final String PROGRAM_DETAIL_EPG ="program_detail_epg";
	  // --------------- dlna --------------------------------------
	public static final String LOCAL_DLNA_NAME = "local_dlna_name";
	public static final String DLNA_SHARE_VIDEO = "is_share_video";
	public static final String DLNA_SHARE_MUSIC = "is_share_music";
	public static final String DLNA_SHARE_IMAGE = "is_share_image";
	public static final String DLNA_SAVE_PLAY_DEVICE_UDN = "dlna_play_device_udn";
	public static final String DLNA_SAVE_RES_DEVICE_UDN = "dlna_res_device_udn";
	
	//**********************Home TV Settings Keys**************
	
	/**
	 * ������ �˿�
	 */
	public static final int PORT=6543;
	//----------һЩЭ��---��������----
	/**
	 * ��½���������� ����
	 */
	public static final byte LOGIN_SERVER_COMMAND=21;
	/**
	 * ��������Ӧ��½������������
	 */
	public static final byte SERVER_RESPONSE_LOAD=22;
	/**
	 * �������� ���� 30
	 */
	public static final byte PLAY_COMMAND=30;
	/**
	 * ֹͣ����
	 */
	public static final byte STOP_COMMAND=31;
	/**
	 * ��ȡURL��ַ �������� 32
	 */
	public static final byte GETURL_COMMAND=32;
	public static final String URL_KEY="URL";
	/**
	 * Զ�̿��� ��������
	 */
	public static final byte REMOTE_CONTROL_COMMAND=40;
	/**
	 * ��ȡƵ���б�
	 */
	public static final byte GET_CHANNEL_LIST=41;
	/**
	 * �ϴ� Ƶ���б�����
	 */
	public static final byte UPLOAD_CHANNEL_CMD=42;
	/**
	 * ɾ��Ƶ������
	 */
	public static final byte DELETE_CHANNEL_CMD=43;
	/**
	 * ����Ƶ����������
	 */
	public static final byte CHANGE_CHANNEL_NAME_CMD=44;
	/**
	 * �ֻ��˻�ȡBOX��ǰAV��·
	 */
	public static final byte QUERY_BOX_CUR_AV_CHANNEL=47;
	/**
	 * BOX����Ӧ ��ѯ��ǰAV��·
	 */
	public static final byte BOX_RESPONSE_CUR_AV_CHANNEL=48;
	
	/**
	 * �ֻ�����BOX����0��1ֹͣ��2��ѯPVR 
	 */
	public static final byte START_STOP_QUERY_PVR=80;
	/**
	 * BOX����Ӧ ��ǰPVR״̬
	 */
	public static final byte BOX_RESPONSE_CUR_PVR_STATE=81;
	/**
	 * �ֻ� �˷�����̨����
	 */
	public static final byte CHANGE_CHANNEL=82;
	
	
	/**
	 * �ֻ��л�AV��·
	 */
	public static final byte CHANGE_AVIN=83;
	
	/**
	 * BOX ����Ӧ����ת������̨
	 */
	public static final byte BOX_RESPONSE_IR_CONTROL=84;
	/**
	 * �ֻ���ȥ��ѯBOX����Ԥ������
	 */
	public static final byte TO_QUERY_BOX_IS_PREVIEWING=85;
	/**
	 * STB��֪ͨ�ֻ��˵�ǰ��Ԥ��ռ�ò��ܲ���
	 */
	public static final byte BOX_RESPONSE_CAN_NOT_PLAY_HINT=86;
	
	public static final byte CMD_HEAD = 90;
	/**
	 *   tvcast ת��
	 */
	public static final byte TVCAST_CMD_TYPE = 50;
	public static final int UDP_PLAY_MSG = 888;
	
	// ��Toast���ٴε�����Ir��ť���ٴΰ��µ���Ϣ����
	public static final int TOAST_CAN_SHOW_AGAIN_MSG=998;
	public static final int IR_CAN_CLICK_AGAIN_MSG=999;
	//����ʱ
	public static final int REQUEST_TIME_OUT=10000;
}
