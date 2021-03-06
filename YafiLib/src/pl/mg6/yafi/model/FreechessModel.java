package pl.mg6.yafi.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.regex.Matcher;

import pl.mg6.common.Settings;
import pl.mg6.yafi.model.data.AdjournedInfo;
import pl.mg6.yafi.model.data.Communication;
import pl.mg6.yafi.model.data.FingerInfo;
import pl.mg6.yafi.model.data.Game;
import pl.mg6.yafi.model.data.HistoryInfo;
import pl.mg6.yafi.model.data.InchannelInfo;
import pl.mg6.yafi.model.data.JournalInfo;
import pl.mg6.yafi.model.data.NewsItem;
import pl.mg6.yafi.model.data.PendingInfo;
import pl.mg6.yafi.model.data.Position;
import pl.mg6.yafi.model.data.ReceivedMessage;
import pl.mg6.yafi.model.data.SeekInfo;
import pl.mg6.yafi.model.data.SeekInfoList;
import pl.mg6.yafi.model.data.VariablesInfo;
import pl.mg6.yafi.model.data.WelcomeData;
import android.util.Log;

public class FreechessModel {
	
	private static final String TAG = FreechessModel.class.getSimpleName();
	
	private Listener listener;
	
	private String output;
	
	private Map<Integer, Game> activeGames;
	private Map<UUID, Game> allGames;
	
	private Map<String, List<Communication>> allCommunication;
	
	private int currentVersion = Integer.MAX_VALUE;
	private boolean currentVersionOld;
	
	private WelcomeData welcomeData;
	private boolean yafiFingered;
	
	public FreechessModel() {
		init();
	}
	
	private void init() {
		output = "";
		activeGames = new HashMap<Integer, Game>();
		allGames = new HashMap<UUID, Game>();
		allCommunication = new HashMap<String, List<Communication>>();
	}
	
	public void setCurrentVersion(int currentVersion) {
		this.currentVersion = currentVersion;
	}
	
	public boolean isCurrentVersionOld() {
		return currentVersionOld;
	}
	
	public String getOutput() {
		return output;
	}
	
	public Game getGame(UUID gameId) {
		return allGames.get(gameId);
	}
	
	public List<UUID> getAllGamesIds() {
		List<UUID> allGamesIds = new ArrayList<UUID>(allGames.size());
		UUID[] array = allGames.keySet().toArray(new UUID[allGames.size()]);
		for (UUID id : array) {
			allGamesIds.add(id);
		}
		return allGamesIds;
	}
	
	public void removeGame(UUID gameId) {
		activeGames.remove(gameId);
		allGames.remove(gameId);
	}
	
	public List<Communication> getCommunicationById(String id) {
		List<Communication> tmp = allCommunication.get(id);
		if (tmp == null) {
			tmp = new ArrayList<Communication>();
			allCommunication.put(id, tmp);
		}
		return new ArrayList<Communication>(tmp);
	}
	
	public void addMessage(Communication c) {
		List<Communication> list = allCommunication.get(c.getId());
		list.add(c);
	}
	
	public List<String> getAllCommunicationIds() {
		List<String> allCommunicationIds = new ArrayList<String>(allCommunication.size());
		String[] array = allCommunication.keySet().toArray(new String[allCommunication.size()]);
		Arrays.sort(array, new Comparator<String>() {
			@Override
			public int compare(String lhs, String rhs) {
				try {
					int l = Integer.parseInt(lhs);
					int r = Integer.parseInt(rhs);
					return l - r;
				} catch (NumberFormatException ex) {
					return lhs.compareToIgnoreCase(rhs);
				}
			}
		});
		for (String id : array) {
			allCommunicationIds.add(id);
		}
		return allCommunicationIds;
	}
	
	public WelcomeData getWelcomeData() {
		return welcomeData;
	}
	
	public boolean parse(String output) {
		Matcher m;
		m = FreechessUtils.HACK_MULTIPLE_ACCEPTS.matcher(output);
		if (m.find()) {
			this.output += m.group() + "yafi% ";
			parse(output.substring(m.end()));
			return true;
		}
		m = FreechessUtils.HACK_POSTED_MANUAL_SEEK.matcher(output);
		if (m.find()) {
			this.output += m.group() + "yafi% ";
			parse(output.substring(m.end()));
			return true;
		}
		m = FreechessUtils.GAMEINFO_MOVE.matcher(output);
		if (m.matches()) {
			parseGameInfoMove(m);
			return false;
		}
		m = FreechessUtils.GAMEINFO_ACCEPT_DECLINE_MOVE.matcher(output);
		if (m.matches()) {
			parseGameInfoAcceptDeclineMove(m);
			this.output += m.group(1) + "yafi% ";
			return true;
		}
		m = FreechessUtils.GAMEINFO_ILLEGAL_MOVE.matcher(output);
		if (m.matches()) {
			parseGameInfoIllegalMove(m);
			this.output += m.group(1) + "yafi% ";
			return true;
		}
		m = FreechessUtils.GAMEINFO_CREATING.matcher(output);
		if (m.matches()) {
			parseGameInfoCreating(m);
			this.output += m.group(1) + m.group(6) + "yafi% ";
			return true;
		}
		m = FreechessUtils.GAMEINFO_OBSERVING.matcher(output);
		if (m.matches()) {
			parseGameInfoObserving(m);
			this.output += m.group(1) + "yafi% ";
			return true;
		}
		m = FreechessUtils.GAMEINFO_FOLLOWING.matcher(output);
		if (m.matches()) {
			parseGameInfoFollowing(m);
			this.output += m.group(1) + "yafi% ";
			return true;
		}
		m = FreechessUtils.GAMEINFO_EXAMINING.matcher(output);
		if (m.matches()) {
			parseGameInfoExamining(m);
			this.output += m.group(1) + "yafi% ";
			return true;
		}
		m = FreechessUtils.GAMEINFO_NOTE_MOVE.matcher(output);
		if (m.matches()) {
			parseGameInfoNoteMove(m);
			this.output += m.group(1) + "yafi% ";
			return true;
		}
		m = FreechessUtils.GAMEINFO_NOTE_MOVE_NOTE.matcher(output);
		if (m.matches()) {
			parseGameInfoNoteMoveNote(m);
			this.output += m.group(1) + m.group(4) + "yafi% ";
			return true;
		}
		m = FreechessUtils.GAMEINFO_MOVE_NOTE.matcher(output);
		if (m.matches()) {
			parseGameInfoMoveNote(m);
			this.output += m.group(2) + "yafi% ";
			return true;
		}
		m = FreechessUtils.GAMEINFO_MORETIME_MOVE.matcher(output);
		if (m.matches()) {
			parseGameInfoMoretimeMove(m);
			this.output += m.group(1) + "yafi% ";
		}
		m = FreechessUtils.GAMEINFO_MOVE_END.matcher(output);
		if (m.matches()) {
			parseGameInfoMoveEnd(m);
			this.output += m.group(2) + "yafi% ";
			return true;
		}
		m = FreechessUtils.GAMEINFO_END_MOVE.matcher(output);
		if (m.matches()) {
			parseGameInfoEndMove(m);
			this.output += m.group(1) + "yafi% ";
			return true;
		}
		m = FreechessUtils.GAMEINFO_AUTOFLAGGING_MOVE.matcher(output);
		if (m.matches()) {
			parseGameInfoAutoflaggingMove(m);
			this.output += m.group(1) + "yafi% ";
			return true;
		}
		m = FreechessUtils.SEEKINFO_SET.matcher(output);
		if (m.matches()) {
			parseSeekInfoSet(m);
			return false;
		}
		m = FreechessUtils.SEEKINFO_SET_ERROR.matcher(output);
		if (m.matches()) {
			parseSeekInfoSetError(m);
			return false;
		}
		m = FreechessUtils.SEEKINFO_SEEK.matcher(output);
		if (m.matches()) {
			parseSeekInfoSeek(m);
			return false;
		}
		m = FreechessUtils.SEEKINFO_REMOVE.matcher(output);
		if (m.matches()) {
			parseSeekInfoRemove(m);
			return false;
		}
		m = FreechessUtils.SEEKINFO_UNSET.matcher(output);
		if (m.matches()) {
			return false;
		}
		m = FreechessUtils.PENDING.matcher(output);
		if (m.matches()) {
			parsePending(m);
			this.output += output + "yafi% ";
			return true;
		}
		m = FreechessUtils.GAMEINFO_REMOVING_OBSERVED.matcher(output);
		if (m.matches()) {
			parseGameInfoRemovingObserved(m);
			this.output += m.group(1) + "yafi% ";
			return true;
		}
		m = FreechessUtils.GAMEINFO_ACCEPT_REMOVING_OBSERVED.matcher(output);
		if (m.matches()) {
			parseGameInfoAcceptRemovingObserved(m);
			this.output += m.group(1) + "yafi% ";
			return true;
		}
		m = FreechessUtils.COMMAND_NOT_FOUND.matcher(output);
		if (m.matches()) {
			if (parseCommandNotFound(m)) {
				this.output += output + "yafi% ";
				return true;
			} else {
				return false;
			}
		}
		m = FreechessUtils.FINGER.matcher(output);
		if (m.matches()) {
			if (parseFinger(m) && !yafiFingered) {
				yafiFingered = true;
				return false;
			} else {
				this.output += output + "yafi% ";
				return true;
			}
		}
		m = FreechessUtils.GAMEINFO_MEXAMINED.matcher(output);
		if (m.matches()) {
			parseGameInfoMexamined(m);
			this.output += m.group(1) + "yafi% ";
			return true;
		}
		this.output += output + "yafi% ";
		m = FreechessUtils.GAMEINFO_REMOVING_EXAMINED.matcher(output);
		if (m.matches()) {
			parseGameInfoRemovingExamined(m);
			return true;
		}
		m = FreechessUtils.DECLINE_MATCH.matcher(output);
		if (m.matches()) {
			parseDeclineMatch(m);
			return true;
		}
		m = FreechessUtils.DECLINED_MATCH.matcher(output);
		if (m.matches()) {
			parseDeclinedMatch(m);
			return true;
		}
		m = FreechessUtils.WITHDRAW_MATCH.matcher(output);
		if (m.matches()) {
			parseWithdrawMatch(m);
			return true;
		}
		m = FreechessUtils.WITHDRAWN_MATCH.matcher(output);
		if (m.matches()) {
			parseWithdrawnMatch(m);
			return true;
		}
		m = FreechessUtils.REMOVED_MATCH.matcher(output);
		if (m.matches()) {
			parseRemovedMatch(m);
			return true;
		}
		m = FreechessUtils.GAMEINFO_NOTE.matcher(output);
		if (m.matches()) {
			parseGameInfoNote(m);
			return true;
		}
		m = FreechessUtils.GAMEINFO_END.matcher(output);
		if (m.matches()) {
			parseGameInfoEnd(m);
			return true;
		}
		m = FreechessUtils.GAMEINFO_NOTE_END.matcher(output);
		if (m.matches()) {
			parseGameInfoNoteEnd(m);
			return true;
		}
		m = FreechessUtils.GAMEINFO_ABORTED_END.matcher(output);
		if (m.matches()) {
			parseGameInfoAbortedEnd(m);
			return true;
		}
		m = FreechessUtils.GAMEINFO_DRAW_OFFER.matcher(output);
		if (m.matches()) {
			parseGameInfoDrawOffer(m);
			return true;
		}
		m = FreechessUtils.GAMEINFO_ABORT_REQUEST.matcher(output);
		if (m.matches()) {
			parseGameInfoAbortRequest(m);
			return true;
		}
		m = FreechessUtils.PRIVATE_TELL.matcher(output);
		if (m.matches()) {
			parsePrivateTell(m);
			return true;
		}
		m = FreechessUtils.SAY.matcher(output);
		if (m.matches()) {
			parseSay(m);
			return true;
		}
		m = FreechessUtils.PARTNER_TELL.matcher(output);
		if (m.matches()) {
			parsePartnerTell(m);
			return true;
		}
		m = FreechessUtils.CHANNEL_TELL.matcher(output);
		if (m.matches()) {
			parseChannelTell(m);
			return true;
		}
		m = FreechessUtils.SHOUT.matcher(output);
		if (m.matches()) {
			parseShout(m);
			return true;
		}
		m = FreechessUtils.SHOUT_IT.matcher(output);
		if (m.matches()) {
			parseShoutIt(m);
			return true;
		}
		m = FreechessUtils.CHESS_SHOUT.matcher(output);
		if (m.matches()) {
			parseChessShout(m);
			return true;
		}
		m = FreechessUtils.ANNOUNCEMENT.matcher(output);
		if (m.matches()) {
			parseAnnouncement(m);
			return true;
		}
		m = FreechessUtils.KIBITZ_WHISPER.matcher(output);
		if (m.matches()) {
			parseKibitzWhisper(m);
			return true;
		}
		m = FreechessUtils.LISTINFO_SHOW.matcher(output);
		if (m.matches()) {
			parseListInfoShow(m);
			return true;
		}
		m = FreechessUtils.LISTINFO_ADD.matcher(output);
		if (m.matches()) {
			parseListInfoAdd(m);
			return true;
		}
		m = FreechessUtils.LISTINFO_SUB.matcher(output);
		if (m.matches()) {
			parseListInfoSub(m);
			return true;
		}
		m = FreechessUtils.FINGER.matcher(output);
		if (m.matches()) {
			parseFinger(m);
			return true;
		}
		m = FreechessUtils.VARIABLES.matcher(output);
		if (m.matches()) {
			parseVariables(m);
			return true;
		}
		m = FreechessUtils.HISTORY.matcher(output);
		if (m.matches()) {
			parseHistory(m);
			return true;
		}
		m = FreechessUtils.JOURNAL.matcher(output);
		if (m.matches()) {
			parseJournal(m);
			return true;
		}
		m = FreechessUtils.ADJOURNED.matcher(output);
		if (m.matches()) {
			parseAdjourned(m);
			return true;
		}
		m = FreechessUtils.NO_HISTORY.matcher(output);
		if (m.matches()) {
			parseNoHistory(m);
			return true;
		}
		m = FreechessUtils.NO_JOURNAL.matcher(output);
		if (m.matches()) {
			parseNoJournal(m);
			return true;
		}
		m = FreechessUtils.PRIVATE_JOURNAL.matcher(output);
		if (m.matches()) {
			parsePrivateJournal(m);
			return true;
		}
		m = FreechessUtils.UNREG_JOURNAL.matcher(output);
		if (m.matches()) {
			parseUnregJournal(m);
			return true;
		}
		m = FreechessUtils.NO_ADJOURNED.matcher(output);
		if (m.matches()) {
			parseNoAdjourned(m);
			return true;
		}
		m = FreechessUtils.INCHANNEL_NUMBER.matcher(output);
		if (m.matches()) {
			parseInchannelNumber(m);
			return true;
		}
		m = FreechessUtils.HANDLE_PREFIX.matcher(output);
		if (m.matches()) {
			parseHandlePrefix(m);
			return true;
		}
		m = FreechessUtils.WHO_IBSLWBSLX.matcher(output);
		if (m.find()) {
			parseWhoIbslwbslx(output);
			return true;
		}
		m = FreechessUtils.CANT_PLAY_VARIANTS_UNTIMED.matcher(output);
		if (m.matches()) {
			parseCantPlayVariantsUntimed(m);
			return true;
		}
		m = FreechessUtils.TIME_CONTROLS_TOO_LARGE.matcher(output);
		if (m.matches()) {
			parseTimeControlsTooLarge(m);
			return true;
		}
		m = FreechessUtils.ALREADY_HAVE_SAME_SEEK.matcher(output);
		if (m.matches()) {
			parseAlreadyHaveSameSeek(m);
			return true;
		}
		m = FreechessUtils.CANNOT_CHALLENGE_WHILE_EXAMINING.matcher(output);
		if (m.matches()) {
			parseCannotChallengeWhileExamining(m);
			return true;
		}
		m = FreechessUtils.CANNOT_CHALLENGE_WHILE_PLAYING.matcher(output);
		if (m.matches()) {
			parseCannotChallengeWhilePlaying(m);
			return true;
		}
		m = FreechessUtils.CAN_HAVE_3_SEEKS.matcher(output);
		if (m.matches()) {
			parseCanHave3Seeks(m);
			return true;
		}
		m = FreechessUtils.SEEK_NOT_AVAILABLE.matcher(output);
		if (m.matches()) {
			parseSeekNotAvailable(m);
			return true;
		}
		m = FreechessUtils.NOT_LOGGED_IN.matcher(output);
		if (m.matches()) {
			parseNotLoggedIn(m);
			return true;
		}
		m = FreechessUtils.MOTD_EXTENDED.matcher(output);
		if (m.find()) {
			parseMotdExtended(m);
			return true;
		}
		m = FreechessUtils.NEWS.matcher(output);
		if (m.find()) {
			parseNews(output);
			return true;
		}
		m = FreechessUtils.NEWS_DETAILS.matcher(output);
		if (m.matches()) {
			parseNewsDetails(m);
			return true;
		}
		m = FreechessUtils.MESSAGES.matcher(output);
		if (m.find()) {
			parseMessages(output);
			return true;
		}
		if (Settings.LOG_SERVER_COMMUNICATION) {
			Log.w(TAG, "not parsed: [" + output.replace("\n\n\n\n", "\n \n \n \n").replace("\n\n\n", "\n \n \n").replace("\n\n", "\n \n") + "]");
		}
		return true;
	}
	
	private void parseGameInfoMove(Matcher m) {
		parseMove(m);
		String additionalMoves = m.group(2);
		if (additionalMoves.length() > 0) {
			m = FreechessUtils.GAMEINFO_MOVE_ADDITIONAL.matcher(additionalMoves);
			while (m.find()) {
				parseMove(m);
			}
		}
	}
	
	private void parseMove(Matcher m) {
		String style12 = m.group(1);
		Position pos = Position.fromStyle12(style12);
		Game game = activeGames.get(pos.getGameId());
		if (game != null) {
			game.addPosition(pos);
			notifyGameUpdate(game.getUUID());
		} else if (pos.getRelation() == Game.RELATION_EXAMINING) {
			game = new Game();
			game.addPosition(pos);
			activeGames.put(game.getId(), game);
			allGames.put(game.getUUID(), game);
			notifyGameCreate(game.getUUID());
			notifyGameUpdate(game.getUUID());
		}
	}
	
	private void parseGameInfoAcceptDeclineMove(Matcher m) {
		String style12 = m.group(2);
		Position pos = Position.fromStyle12(style12);
		Game game = activeGames.get(pos.getGameId());
		if (game != null) {
			game.addPosition(pos);
			notifyGameUpdate(game.getUUID());
		}
	}
	
	private void parseGameInfoIllegalMove(Matcher m) {
		String style12 = m.group(2);
		Position pos = Position.fromStyle12(style12);
		Game game = activeGames.get(pos.getGameId());
		if (game != null) {
			game.addPosition(pos);
			notifyGameUpdate(game.getUUID());
		}
		notifyIllegalMove();
	}
	
	private void parseGameInfoCreating(Matcher m) {
		String removingObservedGames = m.group(2);
		String whiteRating = m.group(3);
		String blackRating = m.group(4);
		String style12 = m.group(5);
		Position pos = Position.fromStyle12(style12);
		Game game = new Game();
		game.setWhiteRating(whiteRating);
		game.setBlackRating(blackRating);
		game.addPosition(pos);
		activeGames.put(game.getId(), game);
		allGames.put(game.getUUID(), game);
		notifyGameCreate(game.getUUID());
		if (removingObservedGames.length() != 0) {
			m = FreechessUtils.GAME_ID.matcher(removingObservedGames);
			while (m.find()) {
				int gameId = Integer.parseInt(m.group());
				game = activeGames.remove(gameId);
				if (game != null) {
					game.setResult("*", "[This game was removed from observation list.]");
					game.setRelation(Game.RELATION_UNKNOWN);
					notifyGameUpdate(game.getUUID());
				}
			}
		}
	}
	
	private void parseGameInfoObserving(Matcher m) {
		String whiteRating = m.group(2);
		String blackRating = m.group(3);
		String style12 = m.group(4);
		Position pos = Position.fromStyle12(style12);
		Game game = new Game();
		game.setWhiteRating(whiteRating);
		game.setBlackRating(blackRating);
		game.addPosition(pos);
		activeGames.put(game.getId(), game);
		allGames.put(game.getUUID(), game);
		notifyGameCreate(game.getUUID());
		notifyGameUpdate(game.getUUID());
	}
	
	private void parseGameInfoFollowing(Matcher m) {
		String removed = m.group(2);
		String whiteRating = m.group(3);
		String blackRating = m.group(4);
		String style12 = m.group(5);
		if (removed != null) {
			m = FreechessUtils.GAME_ID.matcher(removed);
			while (m.find()) {
				int gameId = Integer.parseInt(m.group());
				Game game = activeGames.remove(gameId);
				if (game != null) {
					game.setResult("*", "[This game was removed from observation list.]");
					game.setRelation(Game.RELATION_UNKNOWN);
					notifyGameUpdate(game.getUUID());
				}
			}
		}
		Position pos = Position.fromStyle12(style12);
		Game game = new Game();
		game.setWhiteRating(whiteRating);
		game.setBlackRating(blackRating);
		game.addPosition(pos);
		activeGames.put(game.getId(), game);
		allGames.put(game.getUUID(), game);
		notifyGameCreate(game.getUUID());
		notifyGameUpdate(game.getUUID());
	}
	
	private void parseGameInfoExamining(Matcher m) {
		String style12 = m.group(2);
		Position pos = Position.fromStyle12(style12);
		Game game = new Game();
		game.addPosition(pos);
		activeGames.put(game.getId(), game);
		allGames.put(game.getUUID(), game);
		notifyGameCreate(game.getUUID());
		notifyGameUpdate(game.getUUID());
	}
	
	private void parseGameInfoNoteMove(Matcher m) {
		String note = m.group(2);
		String style12 = m.group(3);
		Position pos = Position.fromStyle12(style12);
		Game game = activeGames.get(pos.getGameId());
		if (game != null) {
			game.addNote(note);
			game.addPosition(pos);
			notifyGameUpdate(game.getUUID());
		}
	}
	
	private void parseGameInfoNoteMoveNote(Matcher m) {
		String note1 = m.group(2);
		String style12 = m.group(3);
		String note2 = m.group(5);
		Position pos = Position.fromStyle12(style12);
		Game game = activeGames.get(pos.getGameId());
		if (game != null) {
			game.addNote(note1);
			game.addPosition(pos);
			game.addNote(note2);
			m = FreechessUtils.GAMEINFO_NOTE_VALUE_RESULT.matcher(note2);
			if (m.matches()) {
				String description = m.group(1);
				String result = m.group(2);
				game.setResult(result, description);
			}
			notifyGameUpdate(game.getUUID());
		}
	}
	
	private void parseGameInfoMoveNote(Matcher m) {
		String style12 = m.group(1);
		String note = m.group(3);
		String note2 = m.group(4);
		Position pos = Position.fromStyle12(style12);
		Game game = activeGames.get(pos.getGameId());
		if (game != null) {
			game.addPosition(pos);
			game.addNote(note);
			if (note2 != null) {
				game.addNote(note2);
			}
			notifyGameUpdate(game.getUUID());
		} else if (pos.getRelation() == Game.RELATION_EXAMINING) {
			game = new Game();
			game.addPosition(pos);
			activeGames.put(game.getId(), game);
			allGames.put(game.getUUID(), game);
			notifyGameCreate(game.getUUID());
			notifyGameUpdate(game.getUUID());
		}
	}
	
	private void parseGameInfoMoretimeMove(Matcher m) {
		String style12 = m.group(2);
		Position pos = Position.fromStyle12(style12);
		Game game = activeGames.get(pos.getGameId());
		if (game != null) {
			game.addPosition(pos);
			notifyGameUpdate(game.getUUID());
		}
	}
	
	private void parseGameInfoMoveEnd(Matcher m) {
		String style12 = m.group(1);
		String description = m.group(3);
		String result = m.group(4);
		Position pos = Position.fromStyle12(style12);
		Game game = activeGames.get(pos.getGameId());
		if (game != null) {
			game.addPosition(pos);
			game.setResult(result, description);
			game.setRelation(Game.RELATION_UNKNOWN);
			notifyGameUpdate(game.getUUID());
		}
	}
	
	private void parseGameInfoAutoflaggingMove(Matcher m) {
		String style12 = m.group(2);
		Position pos = Position.fromStyle12(style12);
		Game game = activeGames.get(pos.getGameId());
		if (game != null) {
			game.addPosition(pos);
			notifyGameUpdate(game.getUUID());
		}
	}
	
	private void parsePending(Matcher m) {
		PendingInfo info = PendingInfo.fromMatch(m);
		notifyPendingInfo(info);
	}
	
	private void parseGameInfoRemovingObserved(Matcher m) {
		if (m.group(2) != null) { // server bug 1: "\n<sr> ...\n" after "removing game ... from observation list" (missing prompt)
			String[] seekIds = m.group(2).split(" ");
			SeekInfoList list = new SeekInfoList();
			for (String id : seekIds) {
				SeekInfo seekInfo = SeekInfo.withId(id);
				list.add(seekInfo);
			}
			notifySeekInfoRemove(list);
		}
		m = FreechessUtils.GAME_ID.matcher(m.group(1));
		while (m.find()) {
			int gameId = Integer.parseInt(m.group());
			Game game = activeGames.remove(gameId);
			if (game != null) {
				game.setResult("*", "[This game was removed from observation list.]");
				game.setRelation(Game.RELATION_UNKNOWN);
				notifyGameUpdate(game.getUUID());
			}
		}
	}
	
	private void parseGameInfoAcceptRemovingObserved(Matcher m) {
		if (m.group(2) != null) { // server bug 1: "\n<sr> ...\n" after "accepts the match offer" (missing prompt)
			String[] seekIds = m.group(2).split(" ");
			SeekInfoList list = new SeekInfoList();
			for (String id : seekIds) {
				SeekInfo seekInfo = SeekInfo.withId(id);
				list.add(seekInfo);
			}
			notifySeekInfoRemove(list);
		}
		m = FreechessUtils.GAME_ID.matcher(m.group(1));
		while (m.find()) {
			int gameId = Integer.parseInt(m.group());
			Game game = activeGames.remove(gameId);
			if (game != null) {
				game.setResult("*", "[This game was removed from observation list.]");
				game.setRelation(Game.RELATION_UNKNOWN);
				notifyGameUpdate(game.getUUID());
			}
		}
	}
	
	private boolean parseCommandNotFound(Matcher m) {
		String cmd = m.group(1);
		if (FreechessUtils.PING_CMD.equals(cmd)) {
			//notifyPingCommand();
			return false;
		} else {
			return true;
		}
	}
	
	private void parseGameInfoRemovingExamined(Matcher m) {
		int gameId = Integer.parseInt(m.group(1));
		Game game = activeGames.remove(gameId);
		if (game != null) {
			game.setResult("*", "[You are no longer examining this game.]");
			game.setRelation(Game.RELATION_UNKNOWN);
			notifyGameUpdate(game.getUUID());
		}
	}
	
	private void parseGameInfoMexamined(Matcher m) {
		String style12 = m.group(2);
		Position pos = Position.fromStyle12(style12);
		Game game = activeGames.get(pos.getGameId());
		if (game != null) {
			game.addPosition(pos);
			notifyGameUpdate(game.getUUID());
		}
	}
	
	private void parseDeclineMatch(Matcher m) {
		String name = m.group(1);
		notifyRemoveMatchOfferFrom(name);
	}
	
	private void parseDeclinedMatch(Matcher m) {
		String name = m.group(1);
		notifyRemoveMatchOfferTo(name);
	}
	
	private void parseWithdrawMatch(Matcher m) {
		String name = m.group(1);
		notifyRemoveMatchOfferTo(name);
	}
	
	private void parseWithdrawnMatch(Matcher m) {
		String name = m.group(1);
		notifyRemoveMatchOfferFrom(name);
	}
	
	private void parseRemovedMatch(Matcher m) {
		String name = m.group(1);
		notifyRemoveMatchOfferFrom(name);
	}
	
	private void parseGameInfoNote(Matcher m) {
		int gameId = Integer.parseInt(m.group(1));
		String note = m.group(2);
		Game game = activeGames.get(gameId);
		if (game != null) {
			game.addNote(note);
			notifyGameUpdate(game.getUUID());
		}
	}
	
	private void parseGameInfoEnd(Matcher m) {
		int gameId = Integer.parseInt(m.group(1));
		String description = m.group(2);
		String result = m.group(3);
		Game game = activeGames.remove(gameId);
		if (game != null) {
			game.setResult(result, description);
			game.setRelation(Game.RELATION_UNKNOWN);
			notifyGameUpdate(game.getUUID());
		}
	}
	
	private void parseGameInfoEndMove(Matcher m) {
		int gameId = Integer.parseInt(m.group(2));
		String description = m.group(3);
		String result = m.group(4);
		String style12 = m.group(5);
		Position pos = Position.fromStyle12(style12);
		Game game = activeGames.remove(gameId);
		if (game != null) {
			game.addPosition(pos);
			game.setResult(result, description);
			game.setRelation(Game.RELATION_UNKNOWN);
			notifyGameUpdate(game.getUUID());
		}
	}
	
	private void parseGameInfoNoteEnd(Matcher m) {
		String note = m.group(1);
		int gameId = Integer.parseInt(m.group(2));
		String description = m.group(3);
		String result = m.group(4);
		Game game = activeGames.remove(gameId);
		if (game != null) {
			game.addNote(note);
			game.setResult(result, description);
			game.setRelation(Game.RELATION_UNKNOWN);
			notifyGameUpdate(game.getUUID());
		}
	}
	
	private void parseGameInfoAbortedEnd(Matcher m) {
		int gameId = Integer.parseInt(m.group(1));
		String description = m.group(2);
		String result = m.group(3);
		Game game = activeGames.remove(gameId);
		if (game != null) {
			game.setResult(result, description);
			game.setRelation(Game.RELATION_UNKNOWN);
			notifyGameUpdate(game.getUUID());
		}
	}
	
	private void parseGameInfoDrawOffer(Matcher m) {
		notifyDrawOffer();
	}
	
	private void parseGameInfoAbortRequest(Matcher m) {
		notifyAbortRequest();
	}
	
	private void parseSeekInfoSet(Matcher m) {
		String seeks = m.group(1);
		m = FreechessUtils.SEEKINFO_SET_SEEK.matcher(seeks);
		SeekInfoList list = new SeekInfoList();
		while (m.find()) {
			SeekInfo seekInfo = SeekInfo.fromMatch(m);
			if (!"crazyhouse".equals(seekInfo.getType())) {
				list.add(seekInfo);
			}
		}
		notifySeekInfoSet(list);
	}
	
	private void parseSeekInfoSetError(Matcher m) {
		notifySeekInfoSetError();
	}
	
	private void parseSeekInfoSeek(Matcher m) {
		SeekInfo seekInfo = SeekInfo.fromMatch(m);
		if (!"crazyhouse".equals(seekInfo.getType())) {
			notifySeekInfoSeek(seekInfo);
		}
	}
	
	private void parseSeekInfoRemove(Matcher m) {
		String[] seekIds = m.group(1).split(" ");
		SeekInfoList list = new SeekInfoList();
		for (String id : seekIds) {
			SeekInfo seekInfo = SeekInfo.withId(id);
			list.add(seekInfo);
		}
		notifySeekInfoRemove(list);
	}
	
	private void parsePrivateTell(Matcher m) {
		String name = m.group(1);
		String message = m.group(2);
		Communication c = Communication.createPrivateTell(name, message);
		handleCommunication(c);
	}
	
	private void parseSay(Matcher m) {
		String name = m.group(1);
		String message = m.group(2);
		Communication c = Communication.createSay(name, message);
		handleCommunication(c);
	}
	
	private void parsePartnerTell(Matcher m) {
		String name = m.group(1);
		String message = m.group(2);
		Communication c = Communication.createPartnerTell(name, message);
		handleCommunication(c);
	}
	
	private void parseChannelTell(Matcher m) {
		String name = m.group(1);
		String channel = m.group(2);
		String message = m.group(3);
		Communication c = Communication.createChannelTell(channel, name, message);
		handleCommunication(c);
	}
	
	private void parseShout(Matcher m) {
		String name = m.group(1);
		String message = m.group(2);
		Communication c = Communication.createShout(name, message);
		handleCommunication(c);
	}
	
	private void parseShoutIt(Matcher m) {
		String name = m.group(1);
		String message = m.group(2);
		Communication c = Communication.createShoutIt(name, message);
		handleCommunication(c);
	}
	
	private void parseChessShout(Matcher m) {
		String name = m.group(1);
		String message = m.group(2);
		Communication c = Communication.createChessShout(name, message);
		handleCommunication(c);
	}
	
	private void parseAnnouncement(Matcher m) {
		String name = m.group(1);
		String message = m.group(2);
		Communication c = Communication.createAnnouncement(name, message);
		handleCommunication(c);
	}
	
	private void parseKibitzWhisper(Matcher m) {
		String name = m.group(1);
		String gameId = m.group(3);
		String message = m.group(5);
		Communication c;
		if (FreechessUtils.ID_KIBITZ.equals(m.group(4))) {
			c = Communication.createKibitz(gameId, name, message);
		} else {
			c = Communication.createWhisper(gameId, name, message);
		}
		Game game = activeGames.get(Integer.parseInt(gameId));
		if (game != null) {
			game.addCommunication(c);
		}
	}
	
	private void parseListInfoShow(Matcher m) {
		String name = m.group(1);
		String entries = m.group(2);
		if ("channel".equals(name)) {
			m = FreechessUtils.LISTINFO_SHOW_ENTRY.matcher(entries);
			while (m.find()) {
				String entry = m.group();
				if (!allCommunication.containsKey(entry)) {
					List<Communication> list = new ArrayList<Communication>();
					allCommunication.put(entry, list);
				}
			}
		}
	}
	
	private void parseListInfoAdd(Matcher m) {
		String entry = m.group(1);
		String name = m.group(2);
		if ("channel".equals(name)) {
			if (!allCommunication.containsKey(entry)) {
				List<Communication> list = new ArrayList<Communication>();
				allCommunication.put(entry, list);
			}
		}
	}
	
	private void parseListInfoSub(Matcher m) {
		String entry = m.group(1);
		String name = m.group(2);
		if ("channel".equals(name)) {
			allCommunication.remove(entry);
		}
	}
	
	private boolean parseFinger(Matcher m) {
		FingerInfo info = FingerInfo.fromMatch(m);
		if ("Yafi".equalsIgnoreCase(info.getUser())) {
			if (info.getLineCount() >= 10) {
				String line = info.getLine(9);
				String[] settings = line.split("#");
				for (String setting : settings) {
					String[] values = setting.split("\\|");
					if ("android".equalsIgnoreCase(values[0])) {
						try {
							int newestVersion = Integer.parseInt(values[Settings.SOURCE_ID]);
							currentVersionOld = newestVersion > currentVersion;
						} catch (NumberFormatException ex) {
							// ignore
						}
					}
				}
			}
			return true;
		}
		notifyFinger(info);
		return false;
	}
	
	private void parseVariables(Matcher m) {
		VariablesInfo info = VariablesInfo.fromMatch(m);
		notifyVariables(info);
	}
	
	private void parseHistory(Matcher m) {
		HistoryInfo info = HistoryInfo.fromMatch(m);
		notifyHistory(info);
	}
	
	private void parseJournal(Matcher m) {
		JournalInfo info = JournalInfo.fromMatch(m);
		notifyJournal(info);
	}
	
	private void parseAdjourned(Matcher m) {
		AdjournedInfo info = AdjournedInfo.fromMatch(m);
		notifyAdjourned(info);
	}
	
	private void parseNoHistory(Matcher m) {
		String user = m.group(1);
		notifyNoHistory(user);
	}
	
	private void parseNoJournal(Matcher m) {
		String user = m.group(1);
		notifyNoJournal(user);
	}
	
	private void parsePrivateJournal(Matcher m) {
		notifyPrivateJournal();
	}
	
	private void parseUnregJournal(Matcher m) {
		notifyUnregJournal();
	}
	
	private void parseNoAdjourned(Matcher m) {
		String user = m.group(1);
		notifyNoAdjourned(user);
	}
	
	private void parseInchannelNumber(Matcher m) {
		String channelNumber = m.group(1);
		String channelName = m.group(2);
		String users = m.group(3);
		InchannelInfo info = new InchannelInfo(channelNumber, channelName);
		m = FreechessUtils.INCHANNEL_USER.matcher(users);
		while (m.find()) {
			String user = m.group(1);
			info.add(user);
		}
		notifyInchannelInfo(info);
	}
	
	private void parseHandlePrefix(Matcher m) {
		String users = m.group(1);
		List<String> list = new ArrayList<String>();
		m = FreechessUtils.LISTINFO_SHOW_ENTRY.matcher(users);
		while (m.find()) {
			String user = m.group();
			list.add(user);
		}
		notifyHandlePrefix(list);
	}
	
	private void parseWhoIbslwbslx(String output) {
		List<String> list = new ArrayList<String>();
		Matcher m = FreechessUtils.WHO_IBSLWBSLX_LINE.matcher(output);
		while (m.find()) {
			String user = m.group(1);
			list.add(user);
		}
		notifyWhoIbslwbslx(list);
	}
	
	private void parseCantPlayVariantsUntimed(Matcher m) {
		notifyCantPlayVariantsUntimed();
	}
	
	private void parseTimeControlsTooLarge(Matcher m) {
		notifyTimeControlsTooLarge();
	}
	
	private void parseAlreadyHaveSameSeek(Matcher m) {
		notifyAlreadyHaveSameSeek();
	}
	
	private void parseCannotChallengeWhileExamining(Matcher m) {
		notifyCannotChallengeWhileExamining();
	}
	
	private void parseCannotChallengeWhilePlaying(Matcher m) {
		notifyCannotChallengeWhilePlaying();
	}
	
	private void parseCanHave3Seeks(Matcher m) {
		notifyCanHave3Seeks();
	}
	
	private void parseSeekNotAvailable(Matcher m) {
		notifySeekNotAvailable();
	}
	
	private void parseNotLoggedIn(Matcher m) {
		String user = m.group(1);
		notifyNotLoggedIn(user);
	}
	
	private void parseMotdExtended(Matcher m) {
		String news = m.group(1);
		String anews = m.group(2);
		String tnews = m.group(3);
		String snews = m.group(4);
		int allMessages = Integer.parseInt(m.group(5));
		int unreadMessages = Integer.parseInt(m.group(6));
		String friendsList = m.group(7);
		String friendsListAlt = m.group(8);
		String[] friends = friendsList != null ? friendsList.split(" ") : new String[0];
		String[] friendsAlt = friendsListAlt != null ? friendsListAlt.split(" ") : new String[0];
		List<NewsItem> newsItems = null;
		if (news != null) {
			newsItems = new ArrayList<NewsItem>();
			m = FreechessUtils.NEWS_ITEM.matcher(news);
			while (m.find()) {
				NewsItem item = NewsItem.fromListMatcher(m);
				newsItems.add(item);
			}
		}
		welcomeData = new WelcomeData(newsItems, unreadMessages, allMessages, friends);
		notifyMotdExtended(welcomeData);
	}
	
	private void parseNews(String output) {
		Matcher m = FreechessUtils.NEWS_ITEM.matcher(output);
		List<NewsItem> newsItems = new ArrayList<NewsItem>();
		while (m.find()) {
			NewsItem item = NewsItem.fromListMatcher(m);
			newsItems.add(item);
		}
		notifyNews(newsItems);
	}
	
	private void parseNewsDetails(Matcher m) {
		NewsItem newsItem = NewsItem.fromDetailsMatcher(m);
		notifyNewsDetails(newsItem);
	}
	
	private void parseMessages(String output) {
		Matcher m = FreechessUtils.MESSAGE_ITEM.matcher(output);
		List<ReceivedMessage> messages = new ArrayList<ReceivedMessage>();
		while (m.find()) {
			ReceivedMessage message = ReceivedMessage.fromMatcher(m);
			messages.add(0, message);
		}
		notifyMessages(messages);
	}
	
	private void handleCommunication(Communication c) {
		String id = c.getId();
		List<Communication> list = allCommunication.get(id);
		if (list == null) {
			list = new ArrayList<Communication>();
			allCommunication.put(id, list);
		}
		list.add(c);
		notifyCommunication(c);
	}
	
	private void notifyGameCreate(UUID gameId) {
		if (listener != null) {
			listener.onGameCreate(gameId);
		}
	}
	
	private void notifyGameUpdate(UUID gameId) {
		if (listener != null) {
			listener.onGameUpdate(gameId);
		}
	}
	
	private void notifyIllegalMove() {
		if (listener != null) {
			listener.onIllegalMove();
		}
	}
	
	private void notifyPendingInfo(PendingInfo info) {
		if (listener != null) {
			listener.onPendingInfo(info);
		}
	}
	
	private void notifyDrawOffer() {
		if (listener != null) {
			listener.onDrawOffer();
		}
	}
	
	private void notifyAbortRequest() {
		if (listener != null) {
			listener.onAbortRequest();
		}
	}
	
	private void notifySeekInfoSet(SeekInfoList seeks) {
		if (listener != null) {
			listener.onSeekInfoSet(seeks);
		}
	}
	
	private void notifySeekInfoSetError() {
		if (listener != null) {
			listener.onSeekInfoSetError();
		}
	}
	
	private void notifySeekInfoSeek(SeekInfo seek) {
		if (listener != null) {
			listener.onReceivedSeek(seek);
		}
	}
	
	private void notifySeekInfoRemove(SeekInfoList list) {
		if (listener != null) {
			listener.onRemovedSeeks(list);
		}
	}
	
	private void notifyCommunication(Communication c) {
		if (listener != null) {
			listener.onCommunication(c);
		}
	}
	
	private void notifyFinger(FingerInfo info) {
		if (listener != null) {
			listener.onFinger(info);
		}
	}
	
	private void notifyVariables(VariablesInfo info) {
		if (listener != null) {
			listener.onVariables(info);
		}
	}
	
	private void notifyHistory(HistoryInfo info) {
		if (listener != null) {
			listener.onHistory(info);
		}
	}
	
	private void notifyJournal(JournalInfo info) {
		if (listener != null) {
			listener.onJournal(info);
		}
	}
	
	private void notifyAdjourned(AdjournedInfo info) {
		if (listener != null) {
			listener.onAdjourned(info);
		}
	}
	
	private void notifyNoHistory(String user) {
		if (listener != null) {
			listener.onNoHistory(user);
		}
	}
	
	private void notifyNoJournal(String user) {
		if (listener != null) {
			listener.onNoJournal(user);
		}
	}
	
	private void notifyPrivateJournal() {
		if (listener != null) {
			listener.onPrivateJournal();
		}
	}
	
	private void notifyUnregJournal() {
		if (listener != null) {
			listener.onUnregJournal();
		}
	}
	
	private void notifyNoAdjourned(String user) {
		if (listener != null) {
			listener.onNoAdjourned(user);
		}
	}
	
	private void notifyInchannelInfo(InchannelInfo info) {
		if (listener != null) {
			listener.onInchannelInfo(info);
		}
	}
	
	private void notifyHandlePrefix(List<String> users) {
		if (listener != null) {
			listener.onHandlePrefix(users);
		}
	}
	
	private void notifyWhoIbslwbslx(List<String> users) {
		if (listener != null) {
			listener.onWhoIbslwbslx(users);
		}
	}
	
	private void notifyRemoveMatchOfferFrom(String user) {
		if (listener != null) {
			listener.onRemoveMatchOfferFrom(user);
		}
	}
	
	private void notifyRemoveMatchOfferTo(String user) {
		if (listener != null) {
			listener.onRemoveMatchOfferTo(user);
		}
	}
	
	private void notifyCantPlayVariantsUntimed() {
		if (listener != null) {
			listener.onCantPlayVariantsUntimed();
		}
	}
	
	private void notifyTimeControlsTooLarge() {
		if (listener != null) {
			listener.onTimeControlsTooLarge();
		}
	}
	
	private void notifyAlreadyHaveSameSeek() {
		if (listener != null) {
			listener.onAlreadyHaveSameSeek();
		}
	}
	
	private void notifyCannotChallengeWhileExamining() {
		if (listener != null) {
			listener.onCannotChallengeWhileExamining();
		}
	}
	
	private void notifyCannotChallengeWhilePlaying() {
		if (listener != null) {
			listener.onCannotChallengeWhilePlaying();
		}
	}
	
	private void notifyCanHave3Seeks() {
		if (listener != null) {
			listener.onCanHave3Seeks();
		}
	}
	
	private void notifySeekNotAvailable() {
		if (listener != null) {
			listener.onSeekNotAvailable();
		}
	}
	
	private void notifyNotLoggedIn(String handle) {
		if (listener != null) {
			listener.onNotLoggedIn(handle);
		}
	}
	
	private void notifyMotdExtended(WelcomeData data) {
		if (listener != null) {
			listener.onMotdExtended(data);
		}
	}
	
	private void notifyNews(List<NewsItem> news) {
		if (listener != null) {
			listener.onNews(news);
		}
	}
	
	private void notifyNewsDetails(NewsItem newsItem) {
		if (listener != null) {
			listener.onNewsDetails(newsItem);
		}
	}
	
	private void notifyMessages(List<ReceivedMessage> messages) {
		if (listener != null) {
			listener.onMessages(messages);
		}
	}
	
	public void setListener(Listener l) {
		listener = l;
	}
	
	public static interface Listener {
		
		void onGameCreate(UUID gameId);
		
		void onGameUpdate(UUID gameId);
		
		void onIllegalMove();
		
		void onPendingInfo(PendingInfo info);
		
		void onDrawOffer();
		
		void onAbortRequest();
		
		void onSeekInfoSet(SeekInfoList seeks);
		
		void onSeekInfoSetError();
		
		void onReceivedSeek(SeekInfo seek);
		
		void onRemovedSeeks(SeekInfoList list);
		
		void onCommunication(Communication c);
		
		void onFinger(FingerInfo info);
		
		void onVariables(VariablesInfo info);
		
		void onHistory(HistoryInfo info);
		
		void onJournal(JournalInfo info);
		
		void onAdjourned(AdjournedInfo info);
		
		void onNoHistory(String user);
		
		void onNoJournal(String user);

		void onPrivateJournal();

		void onUnregJournal();

		void onNoAdjourned(String user);

		void onInchannelInfo(InchannelInfo info);
		
		void onHandlePrefix(List<String> users);
		
		void onWhoIbslwbslx(List<String> users);
		
		void onRemoveMatchOfferFrom(String user);
		
		void onRemoveMatchOfferTo(String user);
		
		void onCantPlayVariantsUntimed();
		
		void onTimeControlsTooLarge();
		
		void onAlreadyHaveSameSeek();
		
		void onCannotChallengeWhileExamining();
		
		void onCannotChallengeWhilePlaying();
		
		void onCanHave3Seeks();
		
		void onSeekNotAvailable();
		
		void onNotLoggedIn(String user);
		
		void onMotdExtended(WelcomeData data);
		
		void onNews(List<NewsItem> news);
		
		void onNewsDetails(NewsItem newsItem);
		
		void onMessages(List<ReceivedMessage> messages);
	}
}
