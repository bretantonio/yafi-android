package pl.mg6.yafi.model;

import java.util.regex.Pattern;

import pl.mg6.yafi.model.data.UserTitle;

public class FreechessUtils {
	
	private static final int USERNAME_MIN_LENGTH = 3;
	private static final int USERNAME_MAX_LENGTH = 17;
	
	private static final StringBuilder builder = new StringBuilder();
	
	private FreechessUtils() {
	}
	
	public static boolean validateUsername(String username) {
		if (username.length() < USERNAME_MIN_LENGTH) {
			return false;
		}
		if (username.length() > USERNAME_MAX_LENGTH) {
			return false;
		}
		for (char c : username.toCharArray()) {
			if (!('A' <= c && c <= 'Z' ||  'a' <= c && c <= 'z')) {
				return false;
			}
		}
		return true;
	}
	
	public static String moveToString(int initFile, int initRank, int destFile, int destRank) {
		builder.setLength(0);
		builder.append((char) ('a' + initFile));
		builder.append((char) ('8' - initRank));
		builder.append((char) ('a' + destFile));
		builder.append((char) ('8' - destRank));
		return builder.toString();
	}
	
	public static boolean isGuest(int titles) {
		return (titles & UserTitle.UNREGISTERED) == UserTitle.UNREGISTERED;
	}
	
	public static final String PING_CMD = "__yafi_pong";
	
	private static final String STYLE_12_X = "\u0007?\n<12> .*\n(?:<b1> game \\d+ white \\[\\w*\\] black \\[\\w*\\]\n)?";
	private static final String STYLE_12 = "\u0007?\n<12> (.*)\n(?:<b1> game \\d+ white \\[\\w*\\] black \\[\\w*\\]\n)?";
	private static final String HANDLE_X = "[A-Za-z]{3,}";
	private static final String HANDLE = "(" + HANDLE_X + ")";
	private static final String TITLES_X = "(?:\\([A-Z*()]+\\))?";
	private static final String TITLES = "(?:\\(([A-Z*()]+)\\))?";
	private static final String OPTIONAL_RATING_ADJUSTMENT = "(?:\n\\w+ rating adjustment: \\d+ --> \\d+\n(?:\\w+ \\w?rank: .*\n)*(?:You have achieved your best active rating so far\\.\n)?|\nNo ratings adjustment done\\.\n)?";
	
	public static final String ID_KIBITZ = "kibitzes";
	
	public static final Pattern HACK_MULTIPLE_ACCEPTS = Pattern.compile("^\n" + HANDLE_X + " accepts your seek\\.\n");
	
	public static final Pattern HACK_POSTED_MANUAL_SEEK = Pattern.compile(
			"^\nYour seek matches one posted by " + HANDLE_X + "\\.\n"
			+ "Issuing match request since the seek was set to manual\\.\n"
			+ "Issuing: " + HANDLE_X + " \\( *[-\\d+]+[PE]?\\) " + HANDLE_X + " \\( *[-\\d+]+[PE]?\\) (?:un)?rated \\S+ \\d+ \\d+\\.\n"
			+ "(?:Your \\w+ rating will change: .*\n"
			+ "Your new RD will be [\\d.]+\n)?");
	
	public static final Pattern PRIVATE_TELL = Pattern.compile("^\n" + HANDLE + TITLES_X + " tells you: (.*)\n$");
	
	public static final Pattern SAY = Pattern.compile("^\n" + HANDLE + TITLES_X + "(?:\\[\\d+\\])? says: (.*)\n$");
	
	public static final Pattern PARTNER_TELL = Pattern.compile("^\n" + HANDLE + TITLES_X + " \\(your partner\\) tells you: (.*)\n$");
	
	public static final Pattern CHANNEL_TELL = Pattern.compile("^\n" + HANDLE + TITLES_X + "\\((\\d+)\\): (.*)\n$");
	
	public static final Pattern SHOUT = Pattern.compile("^\n" + HANDLE + TITLES_X + " shouts: (.*)\n$");
	
	public static final Pattern SHOUT_IT = Pattern.compile("^\n--> " + HANDLE + TITLES_X + " ?(.*)\n$");
	
	public static final Pattern CHESS_SHOUT = Pattern.compile("^\n" + HANDLE + TITLES_X + " c-shouts: (.*)\n$");
	
	public static final Pattern ANNOUNCEMENT = Pattern.compile("^\n\n +\\*\\*ANNOUNCEMENT\\**\\** from " + HANDLE + ": (.*)\n\n$");
	
	public static final Pattern KIBITZ_WHISPER = Pattern.compile("^\n" + HANDLE + TITLES_X + "\\( *([-\\d+]+)[PE]?\\)\\[(\\d+)\\] (kibitzes|whispers): (.*)\n");
	
	public static final Pattern SEEKINFO_SET = Pattern.compile("^seekinfo set\\.\n<sc>\n((?:.*\n)*)$");
	
	public static final Pattern SEEKINFO_SET_SEEK = Pattern.compile("<s> (\\d+) w=" + HANDLE + " ti=(\\w+) rt=(\\d+)([P E]) t=(\\d+) i=(\\d+) r=([ru]) tp=(\\S+) c=([?WB]) rr=(\\d+)-(\\d+) a=([ft]) f=([ft])\n");
	
	public static final Pattern SEEKINFO_SET_ERROR = Pattern.compile("^seekinfo set\\.\n$");
	
	public static final Pattern SEEKINFO_SEEK = Pattern.compile("^\n<s> (\\d+) w=" + HANDLE + " ti=(\\w+) rt=(\\d+)([P E]) t=(\\d+) i=(\\d+) r=([ru]) tp=(\\S+) c=([?WB]) rr=(\\d+)-(\\d+) a=([ft]) f=([ft])\n$");
	
	public static final Pattern SEEKINFO_REMOVE = Pattern.compile("^\n<sr> ([\\d ]+)\n$");
	
	public static final Pattern SEEKINFO_UNSET = Pattern.compile("^seekinfo unset\\.\n$");
	
	public static final Pattern PENDING = Pattern.compile(
			"^(?:There are no offers pending to other players\\.\n|"
			+ "Offers to other players:\n\n"
			+ "((?: *\\d+: .*\n)+)\n"
			+ "If you wish to withdraw any of these offers type \"withdraw number\"\\.\n)"
			+ "\n"
			+ "(?:There are no offers pending from other players\\.\n|"
			+ "Offers from other players:\n\n"
			+ "((?: *\\d+: .*\n)+)\n"
			+ "If you wish to accept any of these offers type \"accept number\"\\.\n"
			+ "If you wish to decline any of these offers type \"decline number\"\\.\n)$");
	
	public static final Pattern DECLINE_MATCH = Pattern.compile("^You decline the match offer from " + HANDLE + "\\.\n$");
	
	public static final Pattern DECLINED_MATCH = Pattern.compile("^\n" + HANDLE + " declines the match offer\\.\n$");
	
	public static final Pattern WITHDRAW_MATCH = Pattern.compile("^You withdraw the match offer to " + HANDLE + "\\.\n$");
	
	public static final Pattern WITHDRAWN_MATCH = Pattern.compile("^\n" + HANDLE + " withdraws the match offer\\.\n$");
	
	public static final Pattern REMOVED_MATCH = Pattern.compile("^\n" + HANDLE + ", who was challenging you, has joined a match with " + HANDLE_X + "\\.\nChallenge from " + HANDLE_X + " removed\\.\n$");
	
	public static final Pattern PENDING_OFFER_TO = Pattern.compile(" *(\\d+): You are offering " + HANDLE + " (?:a (draw)|to (abort|adjourn) the game|to takeback the last (\\d+) half move\\(s\\)|a challenge: " + HANDLE_X + " \\( *[-\\d+]+[PE]?\\) (?:\\[(?:white|black)\\] )?" + HANDLE_X + " \\( *[-\\d+]+[PE]?\\) ((?:un)?rated) (\\S+) (\\d+) (\\d+))\\.\n");
	
	public static final Pattern PENDING_OFFER_FROM = Pattern.compile(" *(\\d+): " + HANDLE + " is offering (?:a (draw)|to (abort|adjourn) the game|to takeback the last (\\d+) half move\\(s\\)|a challenge: " + HANDLE_X + " \\( *[-\\d+]+[PE]?\\) (?:\\[(?:white|black)\\] )?" + HANDLE_X + " \\( *[-\\d+]+[PE]?\\) ((?:un)?rated) (\\S+) (\\d+) (\\d+))\\.\n");
	
	public static final Pattern GAMEINFO_MOVE = Pattern.compile("^" + STYLE_12 + "((?:" + STYLE_12_X + ")*)$");
	
	public static final Pattern GAMEINFO_MOVE_ADDITIONAL = Pattern.compile(STYLE_12);
	
	public static final Pattern GAMEINFO_ACCEPT_DECLINE_MOVE = Pattern.compile("^((?:You (?:accept|decline) the (?:abort|adjourn|draw|switch|takeback) request from " + HANDLE_X + "\\.\n)+)" + STYLE_12 + "$");
	
	public static final Pattern GAMEINFO_ILLEGAL_MOVE = Pattern.compile("^(Illegal move \\(\\S+\\)\\.(?: You must capture\\.)?\n)" + STYLE_12 + "$");
	
	public static final Pattern GAMEINFO_END = Pattern.compile("^\n\\{Game (\\d+) \\(" + HANDLE_X + " vs\\. " + HANDLE_X + "\\) (.*?)\\} (1-0|0-1|1/2-1/2|\\*)\n" + OPTIONAL_RATING_ADJUSTMENT + "$");
	
	public static final Pattern GAMEINFO_NOTE_END = Pattern.compile("^\nGame \\d+: (.*)\n\n\\{Game (\\d+) \\(" + HANDLE_X + " vs\\. " + HANDLE_X + "\\) (.*?)\\} (1-0|0-1|1/2-1/2|\\*)\n" + OPTIONAL_RATING_ADJUSTMENT + "$");
	
	public static final Pattern GAMEINFO_ABORTED_END = Pattern.compile("^(?:The game has been aborted|\nYour opponent has aborted the game) on move one\\.\n\n\\{Game (\\d+) \\(" + HANDLE_X + " vs\\. " + HANDLE_X + "\\) (.*?)\\} (1-0|0-1|1/2-1/2|\\*)\n$");
	
	public static final Pattern GAMEINFO_CREATING = Pattern.compile(
			"^((?:You have an adjourned game with " + HANDLE_X + "; issuing a continuation\\.\n)?"
			+ "(?:Your challenge intercepts " + HANDLE_X + "'s challenge\\.\n|"
			+ "\n" + HANDLE_X + "'s challenge intercepts your challenge\\.\n|"
			+ "You accept the match offer from " + HANDLE_X + "\\.\n|"
			+ "\n" + HANDLE_X + " accepts the match offer\\.\n|"
			+ "Your getgame qualifies for " + HANDLE_X + "'s seek\\.\n|"
			+ "\nYour getgame intercepts " + HANDLE_X + "'s seek\\.\nTurning off getgame mode\\.\n|"
			+ "\n" + HANDLE_X + " accepts your seek\\.\n|"
			+ "(?:Your seek matches one already posted by " + HANDLE_X + "\\.\n"
					+ "Issuing match request since the seek was set to manual\\.\n"
					+ "(?:Issuing: " + HANDLE_X + " \\( *[-\\d+]+[PE]?\\) " + HANDLE_X + " \\( *[-\\d+]+[PE]?\\) (?:un)?rated \\S+ \\d+ \\d+\\.|)\n"
					+ "(?:Your \\w+ rating will change: .*\n"
					+ "Your new RD will be [\\d.]+\n)?\n)*"
					+ "Your seek qualifies for " + HANDLE_X + "'s getgame\\.\n|"
			+ "\nYour seek intercepts " + HANDLE_X + "'s getgame\\.\n(?:Turning off getgame mode\\.\n)?|"
			+ "\n?Your seek matches one (?:already )?posted by " + HANDLE_X + "\\.\n)?"
			+ "((?:Removing game \\d+ from observation list\\.\n)*)"
			+ "(?:Challenge to " + HANDLE_X + " withdrawn\\.\n)*"
			+ "(?:\nChallenge from " + HANDLE_X + " removed\\.\n)*"
			+ "\nCreating: " + HANDLE_X + " \\( *([-\\d+]+)[PE]?\\) " + HANDLE_X + " \\( *([-\\d+]+)[PE]?\\) (?:un)?rated \\S+ \\d+ \\d+(?: \\(adjourned\\))?\n"
			+ "\\{Game \\d+ \\(" + HANDLE_X + " vs\\. " + HANDLE_X + "\\) (?:Creating|Continuing) (?:un)?rated \\S+ match\\.\\}\n)" + STYLE_12 + "((?:\nGame \\d+: .*\n)?)$");
	
	public static final Pattern GAMEINFO_OBSERVING = Pattern.compile("^((?:\n" + HANDLE_X + ", whom you are following, has started (?:a game with " + HANDLE_X + "|examining a game)\\.\n|\nAn observable star game has started\\.\n)?\n{0,2}You are now observing game \\d+\\.\nGame \\d+: " + HANDLE_X + " \\( *([-\\d+]+)[PE]?\\) " + HANDLE_X + " \\( *([-\\d+]+)[PE]?\\) (?:un)?rated \\S+ \\d+ \\d+\n)" + STYLE_12 + "$");
	
	public static final Pattern GAMEINFO_FOLLOWING = Pattern.compile("^((?:(?:You will no longer be following (?:strongest player's|" + HANDLE_X + "'s) games\\.\n)?You will now be following (?:strongest players'|" + HANDLE_X + "'s) games\\.\n|\n?((?:Removing game \\d+ from observation list\\.\n)+))You are now observing game \\d+\\.\nGame \\d+: " + HANDLE_X + " \\( *([-\\d+]+)[PE]?\\) " + HANDLE_X + " \\( *([-\\d+]+)[PE]?\\) (?:un)?rated \\S+ \\d+ \\d+\n)" + STYLE_12 + "$");
	
	public static final Pattern GAMEINFO_EXAMINING = Pattern.compile("^(Starting a game in examine \\(scratch\\) mode\\.\n)" + STYLE_12 + "$");
	
	public static final Pattern GAMEINFO_REMOVING_OBSERVED = Pattern.compile("^(\n?(?:Removing game \\d+ from observation list\\.\n)+)(?:\n<sr> ([\\d ]+)\n)?$");
	
	public static final Pattern GAMEINFO_ACCEPT_REMOVING_OBSERVED = Pattern.compile("^((?:You accept the match offer from " + HANDLE_X + "\\.\n|\n" + HANDLE_X + " accepts the match offer\\.\n)(?:Removing game \\d+ from observation list\\.\n)*)(?:\n<sr> ([\\d ]+)\n)?$");
	
	public static final Pattern GAMEINFO_REMOVING_EXAMINED = Pattern.compile("^\n?You are no longer examining game (\\d+)\\.\n$");
	
	public static final Pattern GAMEINFO_MEXAMINED = Pattern.compile("^(Removing game \\d+ from observation list\\.\n\n" + HANDLE_X + " has made you an examiner of game \\d+\\.\n)" + STYLE_12 + "$");
	
	public static final Pattern GAMEINFO_NOTE = Pattern.compile("^\nGame (\\d+): (.*)\n$");
	
	public static final Pattern GAMEINFO_NOTE_MOVE = Pattern.compile("^(\n?Game \\d+: (.*)\n)" + STYLE_12 + "$");
	
	public static final Pattern GAMEINFO_NOTE_MOVE_NOTE = Pattern.compile("^(\n?Game \\d+: (.*)\n)" + STYLE_12 + "(\nGame \\d+: (.*)\n)$");
	
	public static final Pattern GAMEINFO_NOTE_VALUE_RESULT = Pattern.compile("^(.*) (1-0|0-1|1/2-1/2|\\*)$");
	
	public static final Pattern GAMEINFO_MOVE_NOTE = Pattern.compile("^\n?" + STYLE_12 + "(\nGame \\d+: (.*)\n(?:\nGame \\d+: (.*)\n)?)$");
	
	public static final Pattern GAMEINFO_MORETIME_MOVE = Pattern.compile("^(\\d+ seconds were added to your opponents clock\n)" + STYLE_12 + "$");
	
	public static final Pattern GAMEINFO_MOVE_END = Pattern.compile("^" + STYLE_12 + "(\n\\{Game \\d+ \\(" + HANDLE_X + " vs\\. " + HANDLE_X + "\\) (.*?)\\} (1-0|0-1|1/2-1/2|\\*)\n" + OPTIONAL_RATING_ADJUSTMENT + ")$");
	
	public static final Pattern GAMEINFO_END_MOVE = Pattern.compile("^(\n\\{Game (\\d+) \\(" + HANDLE_X + " vs\\. " + HANDLE_X + "\\) (.*?)\\} (1-0|0-1|1/2-1/2|\\*)\n" + OPTIONAL_RATING_ADJUSTMENT + ")" + STYLE_12 + "$");
	
	public static final Pattern GAMEINFO_AUTOFLAGGING_MOVE = Pattern.compile("^((?:\nChecking if really out of time\\.\n)?\nAuto-flagging\\.\n)" + STYLE_12 + "$");
	
	public static final Pattern GAMEINFO_DRAW_OFFER = Pattern.compile("^\n" + HANDLE + " offers you a draw\\.\n$");
	
	public static final Pattern GAMEINFO_ABORT_REQUEST = Pattern.compile("^\n" + HANDLE + " would like to abort the game; type \"abort\" to accept\\.\n$");
	
	public static final Pattern GAME_ID = Pattern.compile("\\d+");
	
	public static final Pattern LISTINFO_SHOW = Pattern.compile("^-- (\\S+) list: \\d+ \\S+ --\n([\\w \n]+)$");
	
	public static final Pattern LISTINFO_ADD = Pattern.compile("^\\[(\\w+)\\] added to your (\\S+) list\\.\n$");
	
	public static final Pattern LISTINFO_SUB = Pattern.compile("^\\[(\\w+)\\] removed from your (\\S+) list\\.\n$");
	
	public static final Pattern LISTINFO_SHOW_ENTRY = Pattern.compile("\\w+");
	
	public static final Pattern FINGER = Pattern.compile(
			"^Finger of " + HANDLE + TITLES + ":\n" // 1, 2
			+ "\n"
			+ "(?:Last disconnected: (.*)\n|" // 3
			+ HANDLE_X + " has never connected\\.\n|"
			+ "On for: (.*?) +Idle: (.*)\n" // 4, 5
			+ "(?:" + HANDLE_X + " is in (silence) mode\\.\n)?" // 6
			+ "(?:" + HANDLE_X + " is watching for .*\\.\n)?"
			+ "(?:\\(playing game (\\d+): " + HANDLE + " vs\\. " + HANDLE + "\\)\n)?" // 7, 8, 9
			+ "(?:\\(partner is playing game (\\d+): " + HANDLE + " vs\\. " + HANDLE + "\\)\n)?" // 10, 11, 12
			+ "(?:\\(examining game (\\d+): " + HANDLE + " vs\\. " + HANDLE + "\\)\n)?" // 13, 14, 15
			+ "(?:\\(" + HANDLE_X + " is holding a (simul)\\.\\)\n)?" // 16
			+ "(?:\\(" + HANDLE_X + " is observing game\\(s\\) (.*)\\)\n)?" // 17
			+ "(?:\\(" + HANDLE_X + " (.*)\\)\n)?)" // 18
			+ "\n"
			+ "(?:" + HANDLE_X + " has not played any rated games\\.\n|"
			+ " *rating *RD *win *loss *draw *total *best\n"
			+ "(?:Blitz +([-\\d]+) +([\\d.]+) +(\\d+) +(\\d+) +(\\d+) +(\\d+)(?: +(\\d+) +\\(.*\\))?\n)?" // 19
			+ "(?:Standard +([-\\d]+) +([\\d.]+) +(\\d+) +(\\d+) +(\\d+) +(\\d+)(?: +(\\d+) +\\(.*\\))?\n)?" // 26
			+ "(?:Lightning +([-\\d]+) +([\\d.]+) +(\\d+) +(\\d+) +(\\d+) +(\\d+)(?: +(\\d+) +\\(.*\\))?\n)?" // 33
			+ "(?:Wild +([-\\d]+) +([\\d.]+) +(\\d+) +(\\d+) +(\\d+) +(\\d+)(?: +(\\d+) +\\(.*\\))?\n)?" // 40
			+ "(?:Bughouse +([-\\d]+) +([\\d.]+) +(\\d+) +(\\d+) +(\\d+) +(\\d+)(?: +(\\d+) +\\(.*\\))?\n)?" // 47
			+ "(?:Crazyhouse +([-\\d]+) +([\\d.]+) +(\\d+) +(\\d+) +(\\d+) +(\\d+)(?: +(\\d+) +\\(.*\\))?\n)?" // 54
			+ "(?:Suicide +([-\\d]+) +([\\d.]+) +(\\d+) +(\\d+) +(\\d+) +(\\d+)(?: +(\\d+) +\\(.*\\))?\n)?" // 61
			+ "(?:Losers +([-\\d]+) +([\\d.]+) +(\\d+) +(\\d+) +(\\d+) +(\\d+)(?: +(\\d+) +\\(.*\\))?\n)?" // 68
			+ "(?:Atomic +([-\\d]+) +([\\d.]+) +(\\d+) +(\\d+) +(\\d+) +(\\d+)(?: +(\\d+) +\\(.*\\))?\n)?" // 75
			+ ")?\n+"
			+ "(?:Admin Level: .*\n+)?"
			+ "(?:Email *: .*\n+)?"
			+ "(?:Total time online: .*\n+)?"
			+ "(?:[%] of life online: *[\\d.]+ *\\(since .*\\)\n+)?"
			+ "(?:Timeseal [\\d ] : (?:On|Off)\n+)?"
			+ "(?: 1: (.*)\n" // 82
			+ "(?: 2: (.*)\n" // 83
			+ "(?: 3: (.*)\n" // 84
			+ "(?: 4: (.*)\n" // 85
			+ "(?: 5: (.*)\n" // 86
			+ "(?: 6: (.*)\n" // 87
			+ "(?: 7: (.*)\n" // 88
			+ "(?: 8: (.*)\n" // 89
			+ "(?: 9: (.*)\n" // 90
			+ "(?:10: (.*)\n)?)?)?)?)?)?)?)?)?)?$" // 91
			);
	
	public static final Pattern VARIABLES = Pattern.compile(
			"^Variable settings of " + HANDLE + ":\n"
			+ "\n"
			+ "(?:\\w+=\\S+\\s+)*"
			+ "(?:" + HANDLE_X + " is in silence mode\\.\n+)?"
			+ "(?:Prompt: .*\n)?"
			+ "(?:Interface: \"(.*)\"\n)?"
			+ "(?:Bughouse partner: .*\n)?"
			+ "(?:Following: .*\n)?"
			+ "(?:\n f1: (.*)\n"
			+ "(?: f2: (.*)\n"
			+ "(?: f3: (.*)\n"
			+ "(?: f4: (.*)\n"
			+ "(?: f5: (.*)\n"
			+ "(?: f6: (.*)\n"
			+ "(?: f7: (.*)\n"
			+ "(?: f8: (.*)\n"
			+ "(?: f9: (.*)\n)?)?)?)?)?)?)?)?)?"
			+ "(?:\nFormula: (.*)\n)?$"
			);
	
	public static final Pattern HISTORY = Pattern.compile("^\nHistory for " + HANDLE + ":\n +Opponent +Type +ECO +End +Date\n((.*\n)*)$");
	
	public static final Pattern HISTORY_ENTRY = Pattern.compile(" *(\\d+): ([-=+]) +(\\d+) +([WB]) +(\\d+) +" + HANDLE + " +\\[ ([bslwBzSLxun])([ru]) *(\\d+) +(\\d+)\\] +(\\S+) +(\\S+) +(.*)\n");
	
	public static final Pattern JOURNAL = Pattern.compile("^\nJournal for " + HANDLE + ":\n +White +Rating +Black +Rating +Type +ECO +End +Result\n((.*\n)*)$");
	
	public static final Pattern JOURNAL_ENTRY = Pattern.compile("([%]\\d{2}): (\\S+) +(\\d+) +(\\S+) +(\\d+) +\\[ ([bslwBzSLxun])([ru]) *(\\d+) +(\\d+)\\] +(\\S+) +(\\S+) +(1-0|0-1|1/2-1/2|\\*) *\n");
	
	public static final Pattern ADJOURNED = Pattern.compile("^\nStored games for " + HANDLE + ":\n +C +Opponent +On +Type +Str +M +ECO +Date\n((.*\n)*)$");
	
	public static final Pattern ADJOURNED_ENTRY = Pattern.compile(" *\\d+: ([WB]) " + HANDLE + " +([NY]) \\[([ p])([bslwBzSLxun])([ru]) *(\\d+) +(\\d+)\\] +(\\d+)-(\\d+) +(\\S+) +(\\S+) +(.*)\n");
	
	public static final Pattern NO_HISTORY = Pattern.compile("^" + HANDLE + " has no history games\\.\n$");
	
	public static final Pattern NO_JOURNAL = Pattern.compile("^" + HANDLE + " has no journal entries\\.\n$");
	
	public static final Pattern PRIVATE_JOURNAL = Pattern.compile("^That journal is private\\.\n$");
	
	public static final Pattern UNREG_JOURNAL = Pattern.compile("^Only registered players may keep a journal\\.\n$");
	
	public static final Pattern NO_ADJOURNED = Pattern.compile("^" + HANDLE + " has no adjourned games\\.\n$");
	
	public static final Pattern INCHANNEL_NUMBER = Pattern.compile("^Channel (\\d+)(?: \"(\\S+)\")?: (.*)\n\\d+ player(?: is|s are) in channel \\d+\\.\n$");
	
	public static final Pattern INCHANNEL_USER = Pattern.compile("\\{?" + HANDLE + TITLES_X + "\\}?");
	
	public static final Pattern HANDLE_PREFIX = Pattern.compile("^-- Matches: \\d+ player\\(s\\) --\n((.*\n)*)$");
	
	public static final Pattern WHO_IBSLWBSLX = Pattern.compile("^" + HANDLE_X + "[ ^~:#.&][\\da-fA-F]{2}\\d+[P E]\\d+[P E]\\d+[P E]\\d+[P E]\\d+[P E]\\d+[P E]\\d+[P E]\\d+[P E]\\d+[P E],[\\da-fA-F]{2}\n");
	
	public static final Pattern WHO_IBSLWBSLX_LINE = Pattern.compile(HANDLE + "[ ^~:#.&][\\da-f]{2}\\d+[P E]\\d+[P E]\\d+[P E]\\d+[P E]\\d+[P E]\\d+[P E]\\d+[P E]\\d+[P E]\\d+[P E],[\\da-fA-F]{2}\n");
	
	public static final Pattern COMMAND_NOT_FOUND = Pattern.compile("^(\\S+): Command not found\\.\n$");
	
	public static final Pattern CANT_PLAY_VARIANTS_UNTIMED = Pattern.compile("^You can't play chess variants untimed\\.\n$");
	
	public static final Pattern TIME_CONTROLS_TOO_LARGE = Pattern.compile("^The time controls are too large\\.\n$");
	
	public static final Pattern ALREADY_HAVE_SAME_SEEK = Pattern.compile("^You already have an active seek with the same parameters\\.\n$");
	
	public static final Pattern CANNOT_CHALLENGE_WHILE_EXAMINING = Pattern.compile("^You cannot challenge while you are examining a game\\.\n$");
	
	public static final Pattern CANNOT_CHALLENGE_WHILE_PLAYING = Pattern.compile("^You cannot challenge while you are playing a game\\.\n$");
	
	public static final Pattern CAN_HAVE_3_SEEKS = Pattern.compile("^You can only have 3 active seeks\\.\n$");
	
	public static final Pattern SEEK_NOT_AVAILABLE = Pattern.compile("^That seek is not available\\.\n$");
	
	public static final Pattern NOT_LOGGED_IN = Pattern.compile("^" + HANDLE + " is not logged in\\.\n$");
	
	public static final Pattern MOVES = Pattern.compile(
			"^\nMovelist for game (\\d+):\n\n"
			+ HANDLE + " \\((UNR|\\d+)\\) vs\\. " + HANDLE + " \\((UNR|\\d+)\\) --- .*\n"
			+ "(Rated|Unrated) (\\S+) match, initial time: (\\d+) minutes, increment: (\\d+) seconds\\.\n\n"
			+ "(?:<12> (.*)\n\n)?"
			+ "Move  " + HANDLE_X + " +" + HANDLE_X + "\n"
			+ "----  ---------------------   ---------------------\n"
			+ "((?: *\\d+.*\n)*)"
			+ " *\\{(.*)\\} (1-0|0-1|1/2-1/2|\\*)\n\n");
	
	public static final Pattern MOTD_EXTENDED = Pattern.compile(
			"(?:\nThere are no new news items\\.\n|"
			+ "\nIndex of new news items:\n"
			+ "((?:\\d+ .*\n)+)"
			+ "\\(\"news <n>\" will display item number 'n'\\)\n)"
			+ "(?:\nThere are no new anews items\\.\n|"
			+ "\nIndex of new anews items:\n"
			+ "((?:\\d+ .*\n)+)"
			+ "\\(\"anews <n>\" will display item number 'n'\\)\n)?"
			+ "(?:\nThere are no new tnews items\\.\n|"
			+ "\nIndex of new tnews items:\n"
			+ "((?:\\d+ .*\n)+)"
			+ "\\(\"tnews <n>\" will display item number 'n'\\)\n)?"
			+ "(?:\nThere are no new snews items\\.\n|"
			+ "\nIndex of new snews items:\n"
			+ "((?:\\d+ .*\n)+)"
			+ "\\(\"snews <n>\" will display item number 'n'\\)\n)?"
			+ "\n\nYou have (\\d+) messages? \\((\\d+) unread\\)\\.\n"
			+ "Use \"messages u\" to view unread messages and \"clearmessages \\*\" to clear all\\.\n"
			+ "(?:\nYour message file is full, (\\d+) messages is the maximum number allowed\\.\n"
			+ "You will not be able to receive any more messages until you remove some\\.\n)?"
			+ "(?:\nPresent company includes: ([A-Za-z ]+)\\.\n)?"
			+ "(?:\nYour arrival was noted by: ([A-Za-z ]+)\\.\n)?"
			+ "(?:\nYou have \\d+ adjourned games?\\.\n"
			+ "(?:\\d+ players?, who (?:has|have) an adjourned game with you, (?:is|are) online:\n"
			+ "[A-Za-z ]+\n)?)?$");
	
	public static final Pattern NEWS = Pattern.compile("^Index of (?:the last few news items|news items \\d+-\\d+):\n *\\d+ \\(");
	
	public static final Pattern NEWS_ITEM = Pattern.compile(" *(\\d+) \\((.*?)\\) (.*)\n");
	
	public static final Pattern NEWS_DETAILS = Pattern.compile("^ *(\\d+) \\((.*?)\\) (.*)\n\n(.*(?:\n\\\\   .*)*)\n\nPosted by " + HANDLE + "(?: \\(Expires: .*\\))?\\.\n$");
	
	public static final Pattern NEWS_DETAILS_SEPARATOR = Pattern.compile("\n\\\\   ");
	
	public static final Pattern MESSAGES = Pattern.compile("^(?:Messages|Unread messages):\n\\d+\\. ");
	
	public static final Pattern MESSAGE_ITEM = Pattern.compile("(\\d+)\\. " + HANDLE + " at (.*?): (.*)\n");
}
