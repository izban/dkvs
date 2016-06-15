package Zban;

/**
 * Created by izban on 14.06.16.
 */
public final class Constants {
    public static final String PING = "ping";
    public static final String PONG = "PONG";
    public static final String NODE = "node";
    public static final String FAIL = "FAIL";
    public static final String ACCEPTED = "ACCEPTED";
    public static final String GET = "get";
    public static final String VALUE = "VALUE";
    public static final String NOT_FOUND = "NOT_FOUND";
    public static final String SET = "set";
    public static final String STORED = "STORED";
    public static final String DELETE = "delete";
    public static final String DELETED = "DELETED";
    public static final String REQUEST_VOTE = "RequestVote";
    public static final String VOTE_RES = "VoteResult";
    public static final String APPEND_ENTRY = "AppendEntry";
    public static final String RES_APPEND_ENTRY = "ResAppendEntry";
    public static final String MSG_LEADER = "MessageForLeader";
    public static final String LEADER_RESPONSE = "LeaderResponse";

    public static final int TIMEOUT = DKVSProperties.timeout;

    public static final int LEADER_TIMEOUT = 1000;
    public static final int TIMEOUT_RECONNECT = 2 * LEADER_TIMEOUT;
    public static final int ELECTION_TIMEOUT = 5 * LEADER_TIMEOUT;
}
