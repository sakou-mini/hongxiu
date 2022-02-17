package com.donglaistd.jinli.util;

import com.donglaistd.jinli.Constant;
import com.donglaistd.jinli.Jinli;
import com.donglaistd.jinli.database.entity.Card;
import com.donglaistd.jinli.database.entity.Deck;
import com.donglaistd.jinli.database.entity.User;
import com.donglaistd.jinli.database.entity.game.texas.CardsGroup;
import com.donglaistd.jinli.database.entity.game.texas.Texas;
import com.donglaistd.jinli.database.entity.game.texas.RacePokerPlayer;
import com.google.protobuf.Message;

import java.util.*;
import java.util.stream.Collectors;

import static com.donglaistd.jinli.util.MessageUtil.*;

public class TexasUtil {

    // 为正在游戏的玩家分配手牌
    public static void assignHandPoker(Texas texas) {
        texas.getInGamePlayers().forEach(u -> u.setHandPokers(texas.getDeck().dealMultipleCards(2)));
    }

    // 发公共牌
    public static void assignCommonCardByNum(Texas texas, int num) {
        Deck deck = texas.getDeck();
        for (int i = 0; i < num; i++) {
            texas.getCommunityCards().add(deck.deal());
        }
        List<Card> cards = texas.getCommunityCards();
        Jinli.CommonCardBroadcastMessage.Builder message = Jinli.CommonCardBroadcastMessage.newBuilder();
        message.addAllCommonCards(getJinliCard(cards));
        sendMsgToAllPlayers(texas, message);
    }

    public static void sendMessageToInGamePlayers(Texas texas, Message.Builder builder) {
        texas.getInGamePlayers().stream().filter(p -> Objects.nonNull(p.getUser())).forEach(player -> {
            sendMessage(player.getUser().getId(), buildReply(builder));
        });
    }

    public static void sendMsgToAllPlayers(Texas texas, Message.Builder builder) {
        sendMessageToInGamePlayer(texas, builder);
        sendMessageToWaitPlayers(texas, builder);
    }

    public static void sendMsgToAllPlayers(Texas texas, Message.Builder builder, Map<Integer, List<Card>> handCardsMap, Map<Integer, CardsGroup<Constant.TexasType, List<Card>>> finalCardsMap) {
        sendMessageToWaitPlayers(texas, builder);
        Jinli.EndTexasBroadcastMessage.Builder message = (Jinli.EndTexasBroadcastMessage.Builder) builder;
        handCardsMap.forEach((k, v) -> {
            message.addFinalCardsMap(Jinli.CardsMap.newBuilder().setSeatNum(k).addAllCards(getJinliCard(v)).build());
        });
        finalCardsMap.forEach((k, v) -> {
            message.addType(Jinli.TexasCardType.newBuilder().setSeatNum(k).setType(v.getTexasType()));
        });
        sendMessageToInGamePlayer(texas, message);

    }


    public static void sendMessageToInGamePlayer(Texas texas, Message.Builder builder) {
        texas.getInGamePlayers().stream().filter(p -> Objects.nonNull(p.getUser())).forEach(player -> {
            sendMessage(player.getUser().getId(), buildReply(builder));
        });
    }

    public static void sendMessageToWaitPlayers(Texas texas, Message.Builder builder) {
        texas.getWaitPlayers().stream().filter(p -> Objects.nonNull(p.getUser())).forEach(player -> {
            sendMessage(player.getUser().getId(), buildReply(builder));
        });
    }

    // 退出
    public static synchronized void outGame(RacePokerPlayer racePokerPlayer) {
        Texas texas = racePokerPlayer.getTexas();
        removeWaitOrInGamePlayer(racePokerPlayer);
        // 成功则设置
        int index = texas.donePlayerList.indexOf(racePokerPlayer.getSeatNum());
        if (index != -1) {
            texas.donePlayerList.remove(index);
        }
        // 还座位号
        if (racePokerPlayer.getSeatNum() != -1) {
            texas.getFreeSeatStack().push(racePokerPlayer.getSeatNum());
            racePokerPlayer.setSeatNum(-1);
        }
    }

    // 移除等待或游戏中的玩家
    public static void removeWaitOrInGamePlayer(RacePokerPlayer racePokerPlayer) {
        removeWaitPlayer(racePokerPlayer);
        removeInGamePlayer(racePokerPlayer);
    }

    public static void removeWaitPlayer(RacePokerPlayer racePokerPlayer) {
        boolean success = false;
        RacePokerPlayer ret = null;
        if (racePokerPlayer != null && racePokerPlayer.getTexas() != null) {
            Texas texas = racePokerPlayer.getTexas();
            // 移除等待中的玩家
            for (int i = 0; i < texas.getWaitPlayers().size(); i++) {
                RacePokerPlayer b = texas.getWaitPlayers().get(i);
                if (b.getUser().getId().equals(racePokerPlayer.getUser().getId())) {
                    ret = texas.getWaitPlayers().remove(i);
                }
            }
        }
        if (ret != null) {
            success = true;
        }
    }

    public static synchronized boolean removeInGamePlayer(RacePokerPlayer racePokerPlayer) {
        boolean success = false;
        RacePokerPlayer ret = null;
        if (racePokerPlayer != null && racePokerPlayer.getTexas() != null) {
            Texas texas = racePokerPlayer.getTexas();
            // 游戏中的玩家退出
            for (int i = 0; i < texas.getInGamePlayers().size(); i++) {
                RacePokerPlayer p = texas.getInGamePlayers().get(i);
                if (p.getUser().getId().equals(racePokerPlayer.getUser().getId())) {
                    ret = texas.getInGamePlayers().remove(i);
                }
            }
        }
        if (ret != null) {
            success = true;
        }
        return success;
    }

    // 将一个玩家列表中的玩家全部移动到另一个玩家列表中
    public static void movePlayers(List<RacePokerPlayer> from, List<RacePokerPlayer> to) {
        while (from.size() > 0) {
            to.add(from.get(0));// 添加来源列表的首位到目标列表
            from.remove(0);// 移除来源列表的首位
        }
    }

    // 每局开始时更新下一个dealer
    public static void updateNextDealer(Texas texas) {
        int d = texas.getDealer();
        d = getNextSeatNumDealer(d, texas);
        texas.setDealer(d);
    }

    // 获取下一个可操作玩家的座位号
    public static int getNextSeatNum(int seatNum, Texas texas, boolean clockwise) {
        int begin = seatNum;
        while (true) {
            seatNum = getNextNum(seatNum, texas, clockwise);
            RacePokerPlayer pi = getPlayerBySeatNum(seatNum, texas.getInGamePlayers());
            if (pi != null && !pi.isFold() && pi.getBringInChips() != 0) {
                break;
            }
            // 已经循环一圈
            if (begin == seatNum) {
                break;
            }
        }
        return seatNum;
    }

    // 获取下一个可操作玩家的座位号
    public static int getNextSeatNum(int seatNum, Texas texas) {
        int begin = seatNum;
        while (true) {
            seatNum = getNextNum(seatNum, texas);
            RacePokerPlayer pi = getPlayerBySeatNum(seatNum, texas.getInGamePlayers());
            if (pi != null && !pi.isFold() && pi.getBringInChips() != 0) {
                break;
            }
            // 已经循环一圈
            if (begin == seatNum) {
                break;
            }
        }
        return seatNum;
    }

    // 根据座位号返回玩家
    public static RacePokerPlayer getPlayerBySeatNum(int seatNum, List<RacePokerPlayer> playerList) {
        return  playerList.stream().filter(p -> p.getSeatNum() == seatNum).findFirst().orElse(null);
    }

    // 返回下一个座位号
    private static int getNextNum(int seatNum, Texas texas, boolean clockwise) {
        if (clockwise) {
            return getNextNum(seatNum, texas);
        } else {
            int nextSeatNum = seatNum - 1;
            if (nextSeatNum < 0) {
                nextSeatNum = texas.getMaxPlayers() - 1;
            }
            return nextSeatNum;
        }
    }


    // 获取下一个玩家座位号,得到下一个dealer使用
    public static int getNextSeatNumDealer(int seatNum, Texas texas) {
        boolean finded = false;
        int begin = seatNum;
        while (!finded) {
            seatNum = getNextNum(seatNum, texas);
            for (RacePokerPlayer pw : texas.getWaitPlayers()) {
                if (pw.getSeatNum() == seatNum) {
                    finded = true;
                    break;
                }
            }
            for (RacePokerPlayer pi : texas.getInGamePlayers()) {
                if (pi.getSeatNum() == seatNum) {
                    finded = true;
                    break;
                }
            }
            // 已经循环一圈
            if (begin == seatNum) {
                break;
            }
        }
        return seatNum;
    }

    // 返回下一个座位号
    private static int getNextNum(int seatNum, Texas texas) {
        int nextSeatNum = seatNum + 1;
        if (nextSeatNum >= texas.getMaxPlayers()) {
            nextSeatNum = 0;
        }
        return nextSeatNum;
    }

    // 更新下一个轮到的玩家
    public static void updateNextTurn(Texas texas) {
        int thisTurn = texas.getNextTurn();
        thisTurn = getNextSeatNum(thisTurn, texas, true);
        texas.setNextTurn(thisTurn);
    }

    // 按值排序一个map
    public static Map<Integer, Long> sortMapByValue(Map<Integer, Long> oriMap) {
        Map<Integer, Long> sortedMap = new LinkedHashMap<>();
        List<Map.Entry<Integer, Long>> entryList = new ArrayList<>(oriMap.entrySet());
        entryList.sort((entry1, entry2) -> {
            Long value1, value2;
            value1 = entry1.getValue();
            value2 = entry2.getValue();
            return value1.compareTo(value2);
        });
        Iterator<Map.Entry<Integer, Long>> iter = entryList.iterator();
        Map.Entry<Integer, Long> tmpEntry;
        while (iter.hasNext()) {
            tmpEntry = iter.next();
            sortedMap.put(tmpEntry.getKey(), tmpEntry.getValue());
        }
        oriMap.clear();
        return sortedMap;
    }

    public synchronized static void changePlayerChips(RacePokerPlayer racePokerPlayer, Long chips) {
        racePokerPlayer.setBringInChips(racePokerPlayer.getBringInChips() + chips);
    }

    public static Stack<Integer> getStack(int maxPlayer) {
        Stack<Integer> stack = new Stack<>();
        for (int i = 0; i < maxPlayer; i++) {
            stack.push(i);
        }
        return stack;
    }

    public static Jinli.RoundStartBroadcastMessage.Builder buildBetRoundMap(long betAmount, Map<Integer, Long> betRoundMap) {
        Jinli.RoundStartBroadcastMessage.Builder builder = Jinli.RoundStartBroadcastMessage.newBuilder();
        betRoundMap.forEach((k, v) -> {
            builder.addBetRoundMap(Jinli.BetRoundMap.newBuilder().setSeatNum(k).setBetAmount(v).build());
        });
        return builder.setBetAmount(betAmount);
    }

    public static Jinli.UserTexasGame buildUserRaceGameInfo(Texas texas, User user) {
        Jinli.UserTexasGame.Builder builder = Jinli.UserTexasGame.newBuilder();
        ArrayList<RacePokerPlayer> players = new ArrayList<>(texas.getWaitPlayers());
        players.addAll(texas.getInGamePlayers());
        List<Jinli.TexasPokerPlayer> inGamePlayers =
                players.stream().sorted(Comparator.comparing(RacePokerPlayer::getSeatNum)).map(RacePokerPlayer::toProto).collect(Collectors.toList());
        long sum = texas.getBetRoundMap().values().stream().mapToLong(Long::longValue).sum();
        builder.addAllCommunityCards(getJinliCard(texas.getCommunityCards()))
                .setBetAmount(texas.getBetAmount() - sum)
                .setOptTimeout(texas.getConfig().getOptTimeout())
                .setRoundMaxBet(texas.getRoundMaxBet())
                .setDealer(texas.getDealer())
                .setSmallBetSeatNum(texas.getSmallBetSeatNum())
                .setBigBet(texas.getCurrentBigBets())
                .setPreBet(texas.getPreBet())
                .setGameStatus(texas.getGameState().get())
                .setCurrentTurn(texas.getNextTurn())
                .setCountdown(texas.getCountdown())
                .addAllInGamePlayers(inGamePlayers)
                .addAllSeat(texas.getAllSeatNum());
        texas.getBetRoundMap().forEach((k, v) -> {
            builder.addBetRoundMap(Jinli.BetRoundMap.newBuilder().setSeatNum(k).setBetAmount(v));
        });
        RacePokerPlayer player = texas.getWaitPlayers().stream().filter(p -> p.getUser().equals(user)).findFirst().orElse(null);
        if (Objects.isNull(player)) {
            player = texas.getInGamePlayers().stream().filter(p -> p.getUser().equals(user)).findFirst().orElse(null);
        }
        Optional.ofNullable(player).map(RacePokerPlayer::getHandPokers).ifPresent(cards -> builder.addAllHandPokers(getJinliCard(cards)));
        return builder.build();
    }
}
