/**
 * Identifiers of the protocol.
 */
public class Identifiers {

  /**
   * sent when users are logging in
   */
  public static final int CONNECT_MESSAGE = 19;
  /**
   * sent by server side as a response identifier
   */
  public static final int CONNECT_RESPONSE = 20;
  /**
   * sent by server when users are logging off
   */
  public static final int DISCONNECT_MESSAGE = 21;
  /**
   * sent by server side as a response identifier when users are logging off
   */
  public static final int DISCONNECT_RESPONSE = 21;
  /**
   * sent by client side when users type "who" to query active users in system
   */
  public static final int QUERY_CONNECTED_USERS = 22;
  /**
   * sent by server side as a response identifier when users type "who"
   */
  public static final int QUERY_USER_RESPONSE = 23;
  /**
   * sent by client side when users want to send a broadcast message
   */
  public static final int BROADCAST_MESSAGE = 24;
  /**
   * sent by client side when users want to send a direct message
   */
  public static final int DIRECT_MESSAGE = 25;
  /**
   * sent by server side as a response identifier to indicate a failed message
   */
  public static final int FAILED_MESSAGE = 26;
  /**
   * sent by client side when users want to send an insult to someone in system
   */
  public static final int SEND_INSULT = 27;

}
