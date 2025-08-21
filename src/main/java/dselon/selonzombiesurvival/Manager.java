package dselon.selonzombiesurvival;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import dselon.selonzombiesurvival.customevents.*;
import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.block.Block;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;
import org.bukkit.scoreboard.Team;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static dselon.selonzombiesurvival.SelonZombieSurvival.plugin;

public class Manager {

    private String serverTitle; // 서버 이름
    private Location lobbyLocation; // 로비 위치
    private JsonObject mapList; // 맵 목록
    private ArrayList<Integer> playingMapList = new ArrayList<Integer>(); // 플레이한 맵 목록
    private int mapNumber; // 맵 번호
    private String mapName; // 맵 이름
    private String selectedMapName; // 선택된 맵 이름
    private ArrayList<Location> mapLocation = new ArrayList<Location>(); // 맵 위치 목록
    private int restTime; // 대기(쉬는) 시간
    private int readyTime; // 준비(숙주 출현 대기) 시간
    private int roundTime; // 라운드(게임 진행) 시간
    private int roundCount; // 라운드 수
    private int hostStartCount; // 숙주 시작 인원 수
    private int hostIntervalCount; // 숙주 간격 인원 수
    private int hostMaxCount; // 숙주 최대 인원 수
    // 영웅 시스템
    private boolean heroEnable; // 영웅 사용 유무
    private int heroStartCount; // 영웅 시작 인원 수
    private int heroIntervalCount; // 영웅 간격 인원 수
    private int heroMaxCount; // 영웅 최대 인원 수
    // 보균자 시스템
    private boolean carrierEnable; // 보균자 시스템 사용 여부
    private int carrierMinCount; // 보균자 출현에 필요한 참여 플레이어 최소 인원 수
    private int carrierAppearanceCount; // 보균자 출현 횟수
    private float carrierRatio; // 보균자 출현에 필요한 인간과 좀비 비율
    // 보급 시스템
    private boolean supplyEnable; // 보급 시스템 사용 여부
    private int supplyAppearanceCount; // 보급 출현 횟수
    private ArrayList<Location> supplyLocation = new ArrayList<Location>(); // 보급 위치 목록
    // 명령어
    private String commandConvertDefault; // 기본 변환 명령어
    private String commandConvertHuman; // 인간 변환 명령어
    private String commandConvertZombie; // 좀비 변환 명령어
    private String commandConvertHost; // 숙주 변환 명령어
    private String commandConvertHero; // 영웅 변환 명렁어
    private String commandConvertCarrier; // 보균자 변환 명령어
    private String commandAcquiredSupply; // 보급 획득 명령어
    private String commandRewardWin; // 승리 보상 명령어
    private String commandRewardLose; // 패배 보상 명령어
    private String commandRewardKill; // 킬 보상 명령어
    private String commandRewardCure; // 치유 보상 명령어
    private String commandRewardDeath; // 데스 보상 명령어
    private String commandEscapePenalty; // 탈주 패널티 명령어

    public enum GameProgress {IDLE, WAITING, READY, GAME, ENDING}

    private GameProgress gameProgress = GameProgress.IDLE; // 게임 진행 상태
    private int timer; // 타이머
    private int round; // 라운드
    private ArrayList<Player> playerList = new ArrayList<Player>(); // 게임 참여 플레이어 목록
    private ArrayList<Player> humanList = new ArrayList<Player>(); // 인간 플레이어 목록
    private ArrayList<Player> zombieList = new ArrayList<Player>(); // 좀비 플레이어 목록
    private ArrayList<Player> hostList = new ArrayList<Player>(); // 숙주 플레이어 목록
    private ArrayList<Player> wasHostList = new ArrayList<Player>(); // 숙주였던 플레이어 목록
    private ArrayList<Player> livingHumanList = new ArrayList<Player>(); // 생존한 인간 플레이어 목록(좀비 탈출 모드에서 사용됨)
    private ArrayList<Player> heroList = new ArrayList<Player>(); // 영웅 플레이어 목록(영웅 시스템에서 사용됨)
    private ArrayList<Player> wasHeroList = new ArrayList<Player>(); // 영웅이었던 플레이어 목록(영웅 시스템에서 사용됨)
    private ArrayList<Player> carrierList = new ArrayList<Player>(); // 보균자 플레이어 목록(보균자 시스템에서 사용됨)
    private ScoreboardManager scoreboardManager = Bukkit.getScoreboardManager();
    private Scoreboard board = scoreboardManager.getMainScoreboard();
    private Team humanTeam; // 인간 팀
    private Team zombieTeam; // 좀비 팀
    private boolean humanWin; // 인간 승
    private boolean zombieWin; // 좀비 승
    private BossBar bar = Bukkit.createBossBar("", BarColor.WHITE, BarStyle.SOLID);; // 보스 바
    private HashMap<Player, ArrayList<ItemStack>> itemMap = new HashMap<Player, ArrayList<ItemStack>>(); // 플레이어 인벤토리 데이터
    private HashMap<Player, GameMode> modeMap = new HashMap<Player, GameMode>(); // 플레이어 게임 모드 데이터
    private HashMap<Player, Integer> foodLevelMap = new HashMap<Player, Integer>(); // 플레이어 허기 레벨 데이터
    private int task;


    public Manager() {
        this.serverTitle = Files.getServerTitle();
        this.lobbyLocation = Files.getLobbyLocation();
        this.mapList = Files.getMapList();
        this.mapNumber = 0;
        this.selectedMapName = "";

        if (board.getTeam("Human") == null) {
            humanTeam = board.registerNewTeam("Human");
        } else {
            humanTeam = board.getTeam("Human");
        }
        Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> { Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "scoreboard teams option Human color BLUE"); }, 1L);
        humanTeam.setColor(ChatColor.BLUE);
        humanTeam.setAllowFriendlyFire(false);
        humanTeam.setCanSeeFriendlyInvisibles(true);
        humanTeam.setOption(Team.Option.COLLISION_RULE, Team.OptionStatus.ALWAYS);
        humanTeam.setOption(Team.Option.NAME_TAG_VISIBILITY, Team.OptionStatus.FOR_OTHER_TEAMS);
        if (board.getTeam("Zombie") == null) {
            zombieTeam = board.registerNewTeam("Zombie");
        } else {
            zombieTeam = board.getTeam("Zombie");
        }
        Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> { Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "scoreboard teams option Zombie color RED"); }, 1L);
        zombieTeam.setColor(ChatColor.RED);
        zombieTeam.setAllowFriendlyFire(false);
        zombieTeam.setCanSeeFriendlyInvisibles(true);
        zombieTeam.setOption(Team.Option.COLLISION_RULE, Team.OptionStatus.ALWAYS);
        zombieTeam.setOption(Team.Option.NAME_TAG_VISIBILITY, Team.OptionStatus.FOR_OTHER_TEAMS);
        bar.setVisible(false);
    }


    public String getServerTitle() {
        return serverTitle;
    }

    public void setServerTitle(String serverTitle) {
        this.serverTitle = serverTitle;
    }

    public Location getLobbyLocation() {
        return lobbyLocation;
    }

    public void setLobbyLocation(Location lobbyLocation) {
        this.lobbyLocation = lobbyLocation;
    }

    public JsonObject getMapList() {
        return mapList;
    }

    public void setMapList(JsonObject mapList) {
        this.mapList = mapList;
    }

    public int getMapNumber() {
        return mapNumber;
    }

    public ArrayList<Integer> getPlayingMapList() {
        return playingMapList;
    }

    public void setPlayingMapList(ArrayList<Integer> playingMapList) {
        this.playingMapList = playingMapList;
    }

    public void setMapNumber(int mapNumber) {
        this.mapNumber = mapNumber;
    }

    public String getMapName() {
        return mapName;
    }

    public void setMapName(String mapName) {
        this.mapName = mapName;
    }

    public String getSelectedMapName() {
        return selectedMapName;
    }

    public void setSelectedMapName(String selectedMapName) {
        this.selectedMapName = selectedMapName;
    }

    public ArrayList<Location> getMapLocation() {
        return mapLocation;
    }

    public void setMapLocation(ArrayList<Location> mapLocation) {
        this.mapLocation = mapLocation;
    }

    public int getRestTime() {
        return restTime;
    }

    public void setRestTime(int restTime) {
        this.restTime = restTime;
    }

    public int getReadyTime() {
        return readyTime;
    }

    public void setReadyTime(int readyTime) {
        this.readyTime = readyTime;
    }

    public int getRoundTime() {
        return roundTime;
    }

    public void setRoundTime(int roundTime) {
        this.roundTime = roundTime;
    }

    public int getRoundCount() {
        return roundCount;
    }

    public void setRoundCount(int roundCount) {
        this.roundCount = roundCount;
    }

    public int getHostStartCount() {
        return hostStartCount;
    }

    public void setHostStartCount(int hostStartCount) {
        this.hostStartCount = hostStartCount;
    }

    public int getHostIntervalCount() {
        return hostIntervalCount;
    }

    public void setHostIntervalCount(int hostIntervalCount) {
        this.hostIntervalCount = hostIntervalCount;
    }

    public int getHostMaxCount() {
        return hostMaxCount;
    }

    public void setHostMaxCount(int hostMaxCount) {
        this.hostMaxCount = hostMaxCount;
    }

    public boolean isHeroEnable() {
        return heroEnable;
    }

    public void setHeroEnable(boolean heroEnable) {
        this.heroEnable = heroEnable;
    }

    public int getHeroStartCount() {
        return heroStartCount;
    }

    public void setHeroStartCount(int heroStartCount) {
        this.heroStartCount = heroStartCount;
    }

    public int getHeroIntervalCount() {
        return heroIntervalCount;
    }

    public void setHeroIntervalCount(int heroIntervalCount) {
        this.heroIntervalCount = heroIntervalCount;
    }

    public int getHeroMaxCount() {
        return heroMaxCount;
    }

    public void setHeroMaxCount(int heroMaxCount) {
        this.heroMaxCount = heroMaxCount;
    }

    public boolean isCarrierEnable() {
        return carrierEnable;
    }

    public void setCarrierEnable(boolean carrierEnable) {
        this.carrierEnable = carrierEnable;
    }

    public int getCarrierMinCount() {
        return carrierMinCount;
    }

    public void setCarrierMinCount(int carrierMinCount) {
        this.carrierMinCount = carrierMinCount;
    }

    public int getCarrierAppearanceCount() {
        return carrierAppearanceCount;
    }

    public void setCarrierAppearanceCount(int carrierAppearanceCount) {
        this.carrierAppearanceCount = carrierAppearanceCount;
    }

    public float getCarrierRatio() {
        return carrierRatio;
    }

    public void setCarrierRatio(float carrierRatio) {
        this.carrierRatio = carrierRatio;
    }

    public boolean isSupplyEnable() {
        return supplyEnable;
    }

    public void setSupplyEnable(boolean supplyEnable) {
        this.supplyEnable = supplyEnable;
    }

    public int getSupplyAppearanceCount() {
        return supplyAppearanceCount;
    }

    public void setSupplyAppearanceCount(int supplyAppearanceCount) {
        this.supplyAppearanceCount = supplyAppearanceCount;
    }

    public ArrayList<Location> getSupplyLocation() {
        return supplyLocation;
    }

    public void setSupplyLocation(ArrayList<Location> supplyLocation) {
        this.supplyLocation = supplyLocation;
    }

    public String getCommandConvertDefault() {
        return commandConvertDefault;
    }

    public void setCommandConvertDefault(String commandConvertDefault) {
        this.commandConvertDefault = commandConvertDefault;
    }

    public String getCommandConvertHuman() {
        return commandConvertHuman;
    }

    public void setCommandConvertHuman(String commandConvertHuman) {
        this.commandConvertHuman = commandConvertHuman;
    }

    public String getCommandConvertZombie() {
        return commandConvertZombie;
    }

    public void setCommandConvertZombie(String commandConvertZombie) {
        this.commandConvertZombie = commandConvertZombie;
    }

    public String getCommandConvertHost() {
        return commandConvertHost;
    }

    public void setCommandConvertHost(String commandConvertHost) {
        this.commandConvertHost = commandConvertHost;
    }

    public String getCommandConvertHero() {
        return commandConvertHero;
    }

    public void setCommandConvertHero(String commandConvertHero) {
        this.commandConvertHero = commandConvertHero;
    }

    public String getCommandConvertCarrier() {
        return commandConvertCarrier;
    }

    public void setCommandConvertCarrier(String commandConvertCarrier) {
        this.commandConvertCarrier = commandConvertCarrier;
    }

    public String getCommandAcquiredSupply() {
        return commandAcquiredSupply;
    }

    public void setCommandAcquiredSupply(String commandAcquiredSupply) {
        this.commandAcquiredSupply = commandAcquiredSupply;
    }

    public String getCommandRewardWin() {
        return commandRewardWin;
    }

    public void setCommandRewardWin(String commandRewardWin) {
        this.commandRewardWin = commandRewardWin;
    }

    public String getCommandRewardLose() {
        return commandRewardLose;
    }

    public void setCommandRewardLose(String commandRewardLose) {
        this.commandRewardLose = commandRewardLose;
    }

    public String getCommandRewardKill() {
        return commandRewardKill;
    }

    public void setCommandRewardKill(String commandRewardKill) {
        this.commandRewardKill = commandRewardKill;
    }

    public String getCommandRewardCure() {
        return commandRewardCure;
    }

    public void setCommandRewardCure(String commandRewardCure) {
        this.commandRewardCure = commandRewardCure;
    }

    public String getCommandRewardDeath() {
        return commandRewardDeath;
    }

    public void setCommandRewardDeath(String commandRewardDeath) {
        this.commandRewardDeath = commandRewardDeath;
    }

    public String getCommandEscapePenalty() {
        return commandEscapePenalty;
    }

    public void setCommandEscapePenalty(String commandEscapePenalty) {
        this.commandEscapePenalty = commandEscapePenalty;
    }

    public GameProgress getGameProgress() {
        return gameProgress;
    }

    public void setGameProgress(GameProgress gameProgress) {
        this.gameProgress = gameProgress;
    }

    public int getTimer() {
        return timer;
    }

    public void setTimer(int timer) {
        this.timer = timer;
    }

    public int getRound() {
        return round;
    }

    public void setRound(int round) {
        this.round = round;
    }

    public ArrayList<Player> getPlayerList() {
        return playerList;
    }

    public void setPlayerList(ArrayList<Player> playerList) {
        this.playerList = playerList;
    }

    public ArrayList<Player> getHumanList() {
        return humanList;
    }

    public void setHumanList(ArrayList<Player> humanList) {
        this.humanList = humanList;
    }

    public ArrayList<Player> getZombieList() {
        return zombieList;
    }

    public void setZombieList(ArrayList<Player> zombieList) {
        this.zombieList = zombieList;
    }

    public ArrayList<Player> getHostList() {
        return hostList;
    }

    public void setHostList(ArrayList<Player> hostList) {
        this.hostList = hostList;
    }

    public ArrayList<Player> getWasHostList() {
        return wasHostList;
    }

    public void setWasHostList(ArrayList<Player> wasHostList) {
        this.wasHostList = wasHostList;
    }

    public ArrayList<Player> getLivingHumanList() {
        return livingHumanList;
    }

    public void setLivingHumanList(ArrayList<Player> livingHumanList) {
        this.livingHumanList = livingHumanList;
    }

    public ArrayList<Player> getHeroList() {
        return heroList;
    }

    public void setHeroList(ArrayList<Player> heroList) {
        this.heroList = heroList;
    }

    public ArrayList<Player> getWasHeroList() {
        return wasHeroList;
    }

    public void setWasHeroList(ArrayList<Player> wasHeroList) {
        this.wasHeroList = wasHeroList;
    }

    public ArrayList<Player> getCarrierList() {
        return carrierList;
    }

    public void setCarrierList(ArrayList<Player> carrierList) {
        this.carrierList = carrierList;
    }

    public ScoreboardManager getScoreboardManager() {
        return scoreboardManager;
    }

    public void setScoreboardManager(ScoreboardManager scoreboardManager) {
        this.scoreboardManager = scoreboardManager;
    }

    public Scoreboard getBoard() {
        return board;
    }

    public void setBoard(Scoreboard board) {
        this.board = board;
    }

    public Team getHumanTeam() {
        return humanTeam;
    }

    public void setHumanTeam(Team humanTeam) {
        this.humanTeam = humanTeam;
    }

    public Team getZombieTeam() {
        return zombieTeam;
    }

    public void setZombieTeam(Team zombieTeam) {
        this.zombieTeam = zombieTeam;
    }

    public boolean isHumanWin() {
        return humanWin;
    }

    public void setHumanWin(boolean humanWin) {
        this.humanWin = humanWin;
    }

    public boolean isZombieWin() {
        return zombieWin;
    }

    public void setZombieWin(boolean zombieWin) {
        this.zombieWin = zombieWin;
    }

    public BossBar getBar() {
        return bar;
    }

    public void setBar(BossBar bar) {
        this.bar = bar;
    }

    public HashMap<Player, ArrayList<ItemStack>> getItemMap() {
        return itemMap;
    }

    public void setItemMap(HashMap<Player, ArrayList<ItemStack>> itemMap) {
        this.itemMap = itemMap;
    }

    public int getTask() {
        return task;
    }

    public void setTask(int task) {
        this.task = task;
    }


    // 대기 함수
    protected void Waiting() {
        gameProgress = GameProgress.WAITING; // 게임 진행 상태를 대기로 초기화

        round = 0; // 라운드 초기화
        selectMap(); // 맵 선정
        bar.setProgress(0);
        plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, () -> {
            bar.setColor(BarColor.WHITE);
        }, 20L);
        
        timer = restTime + 1; // 대기 시간으로 타이머 초기화
        task = plugin.getServer().getScheduler().scheduleSyncRepeatingTask(plugin, () -> {
            timer--; // 타이머 감소
            bar.setProgress((double)timer / (double)restTime);
            bar.setTitle("§f§l[ §e§l" + mapName + " §f§l] [ §c§l" + timer + "초 §f§l]");

            // TimerEvent 이벤트 호출
            TimerEvent event = new TimerEvent(timer);
            Bukkit.getServer().getPluginManager().callEvent(event);

            // 강제 중지될 경우
            if (gameProgress == GameProgress.IDLE) {
                Bukkit.getScheduler().cancelTask(task);
                return;
            }

            // 타이머 종료
            if (timer == 0) {
                // 게임 참여 플레이어 인원 검사
                if (playerList.size() >= 2) { // 게임 참여 플레이어 인원이 충족될 경우
                    round = 0; // 라운드 초기화
                    Bukkit.getScheduler().cancelTask(task); // 태스크 종료

                    for(Player player : playerList) {
                        backupPlayerData(player); // 플레이어 데이터 백업
                    }

                    Ready(); // 준비 함수 실행
                    return;
                } else { // 게임 참여 플레이어 인원이 부족할 경우
                    Bukkit.getScheduler().cancelTask(task); // 태스크 종료

                    if (restTime > 0) { // 대기 시간이 0 초과일 경우에만 메시지 전송
                        for (Player player : playerList) {
                            player.sendMessage(serverTitle + " §f§l플레이어 인원이 부족하여 타이머를 다시 되돌립니다.");
                        }
                    }

                    Waiting(); // 대기 함수 재실행
                    return;
                }
            }

            // 타이머 메시지 전송
            if (timer % 10 == 0 || timer <= 5) {
                for (Player player : playerList) {
                    player.sendMessage(serverTitle + " §f§l게임 시작까지 §c§l" + timer + "초 §f§l남았습니다.");
                }
            }

        }, 20L, 20L);
    }

    // 준비 함수
    protected void Ready() {
        gameProgress = GameProgress.READY; // 게임 진행 상태를 준비로 초기화

        // RoundStartEvent 이벤트 호출
        RoundStartEvent event = new RoundStartEvent(round);
        Bukkit.getServer().getPluginManager().callEvent(event);

        round++; // 라운드 증가
        for(Player player : playerList) {
            humanList.add(player); // 인간 목록에 플레이어 추가
            humanTeam.addEntry(player.getName()); // 인간 팀에 플레이어 추가
            moveMap(player); // 맵으로 이동
            convertDefault(player); // 기본 변환 함수 호출
            player.sendMessage(serverTitle + " §f§l라운드가 시작되었습니다.");
        }
        for(Player player : humanList) {
            convertHuman(player); // 인간 변환
            player.sendTitle("§9§l생존자", "§c좀비§f를 피해 생존하십시오!", 5, 100, 5);
        }
        bar.setProgress(0);
        plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, () -> {
            bar.setColor(BarColor.YELLOW);
        }, 20L);

        timer = readyTime + 1; // 준비 시간으로 타이머 초기화
        task = plugin.getServer().getScheduler().scheduleSyncRepeatingTask(plugin, () -> {
            timer--; // 타이머 감소
            bar.setProgress((double)timer / (double)readyTime);
            bar.setTitle("§f§l[ §e§l" + round + "라운드 §f§l] [ §c§l" + timer + "초 §f§l]");

            // TimerEvent 이벤트 호출
            TimerEvent timerEvent = new TimerEvent(timer);
            Bukkit.getServer().getPluginManager().callEvent(timerEvent);

            // 강제 중지될 경우
            if (gameProgress == GameProgress.IDLE) {
                Bukkit.getScheduler().cancelTask(task);
                return;
            }
            
            // 타이머 종료
            if (timer == 0) {
                Bukkit.getScheduler().cancelTask(task); // 태스크 종료
                Game();
                return;
            }

            // 타이머 메시지 전송
            if (timer % 10 == 0 || timer <= 5) {
                for (Player player : playerList) {
                    player.sendMessage(serverTitle + " §c§l숙주 §f§l출현까지 §c§l" + timer + "초 §f§l남았습니다.");
                }
            }

        }, 20L, 20L);
    }

    // 게임 함수
    protected void Game() {
        gameProgress = GameProgress.GAME; // 게임 진행 상태를 게임으로 초기화

        selectHost(); // 숙주 선정
        for(Player player : playerList) {
            player.sendMessage(serverTitle + " §c§l숙주§f§l가 출현하였습니다.");
        }
        teamChangeHost(); // 숙주 팀 변경

        // 영웅 시스템
        if (heroEnable) { // 영웅 시스템이 활성화되어 있을 경우
            selectHero(); // 영웅 선정
            for (Player player : heroList) {
                convertHero(player); // 영웅 변환
                player.sendTitle("§9§l영웅", "§9인간§f을 수호하십시오!", 5, 100, 5);
            }
        }

        bar.setProgress(0);
        plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, () -> {
            bar.setColor(BarColor.RED);
        }, 20L);
        
        timer = roundTime + 1; // 라운드 시간으로 타이머 초기화
        task = plugin.getServer().getScheduler().scheduleSyncRepeatingTask(plugin, () -> {
            timer--; // 타이머 감소
            bar.setProgress((double)timer / (double)roundTime);
            bar.setTitle("§f§l[ §e§l" + round + "라운드 §f§l] [ §c§l" + timer + "초 §f§l]");

            // TimerEvent 이벤트 호출
            TimerEvent event = new TimerEvent(timer);
            Bukkit.getServer().getPluginManager().callEvent(event);

            // 강제 중지될 경우
            if (gameProgress == GameProgress.IDLE) {
                Bukkit.getScheduler().cancelTask(task);
                return;
            }

            // 좀비 승리
            if (zombieWin || humanList.size() == 0) {
                Bukkit.getScheduler().cancelTask(task); // 태스크 종료

                // ZombieWinEvent 이벤트 호출
                ZombieWinEvent zombieWinEvent = new ZombieWinEvent();
                Bukkit.getServer().getPluginManager().callEvent(zombieWinEvent);

                for(Player player : playerList) {
                    player.sendMessage(serverTitle + " §c§l좀비§f§l가 승리하였습니다.");
                    player.sendTitle("§c§l좀비 §f§l승리", "", 5, 100, 5);
                    convertDefault(player); // 기본 변환 함수 실행
                }
                for(Player player : humanList) {
                    rewardLose(player); // 패배 보상 함수 실행
                }
                for(Player player : zombieList) {
                    if (hostList.contains(player) || carrierList.contains(player)) { // 플레이어가 숙주 또는 보균자일 경우
                        rewardWin(player); // 승리 보상 함수 실행
                    } else { // 플레이어가 숙주가 아닐 경우
                        rewardLose(player); // 패배 보상 함수 실행
                    }
                }

                Ending();
                return;
            }
            
            // 인간 승리
            if (humanWin || zombieList.size() == 0 || timer == 0) {
                Bukkit.getScheduler().cancelTask(task); // 태스크 종료

                // HumanWinEvent 이벤트 호출
                HumanWinEvent humanWinEvent = new HumanWinEvent();
                Bukkit.getServer().getPluginManager().callEvent(humanWinEvent);
                
                for(Player player : playerList) {
                    player.sendMessage(serverTitle + " §9§l인간§f§l이 승리하였습니다.");
                    player.sendTitle("§9§l인간 §f§l승리", "", 5, 100, 5);
                    convertDefault(player); // 기본 변환 함수 실행
                }
                for(Player player : humanList) {
                    if (humanWin) {
                        if (livingHumanList.contains(player)) { // 플레이어가 생존한 인간일 경우
                            rewardWin(player); // 승리 보상 함수 실행
                        } else { // 플레이어가 생존한 인간이 아닐 경우
                            rewardLose(player); // 패배 보상 함수 실행
                        }
                    } else {
                        rewardWin(player); // 승리 보상 함수 실행
                    }
                }
                for(Player player : zombieList) {
                    rewardLose(player); // 패배 보상 함수 실행
                }

                Ending();
                return;
            }

            // 보균자 시스템
            boolean isExpressed = false;
            if (carrierEnable && playerList.size() >= carrierMinCount) {
                // 보균자 출현 시간 구하기
                int[] appearanceCarrierTime = new int[carrierAppearanceCount];
                double interval = roundTime / (carrierAppearanceCount + 1.0);  // 분모를 double로 변환
                for (int i = 0; i < carrierAppearanceCount; i++) {
                    appearanceCarrierTime[i] = (int)Math.round(interval * (i+1));  // 결과를 반올림하여 정수형으로 변환
                }

                for (int i = 0; i < carrierAppearanceCount; i++) {
                    // 보균자 감지
                    if (humanList.size() >= zombieList.size() * carrierRatio) { // 보균자 출현에 필요한 인간과 좀비 비율이 충족될 경우
                        if (timer == appearanceCarrierTime[i] + 30) {
                            // 생존자 중에서 보균자 선정
                            Player carrier = selectCarrier(); // 보균자 선정
                            if (carrier != null) {
                                for (Player player : playerList) {
                                    player.sendMessage(serverTitle + " §c§l보균자§f§l가 감지되었습니다!");
                                }

                                carrier.sendTitle("§c§l보균자", "§f곧 §c좀비§f가 됩니다!", 5, 100, 5);
                                carrier.sendMessage(serverTitle + " §c§l보균자§f§l로 선정되셨습니다!");
                            }
                        }
                    } else { // 보균자 출현에 필요한 인간과 좀비 비율이 충족되지 않을 경우
                        if (timer == appearanceCarrierTime[i] + 30) {
                            // 감염자 중에서 보균자 선정
                            Player carrier = selectCarrier(); // 보균자 선정
                            if (carrier != null) {
                                // 선정된 감염자를 보균자로 승격
                                for (Player player : playerList) {
                                    player.sendMessage(serverTitle + " §c§l보균자§f§l가 발현되었습니다!");
                                }

                                infecteeChangeCarrier(carrier); // 감염자를 보균자로 변경
                                carrier.sendMessage(serverTitle + " §c§l보균자§f§l로 선정되셨습니다!");
                            }
                        }
                    }
                    for (Player player : carrierList) {
                        if (humanList.contains(player)) { // 플레이어가 인간일 경우
                            isExpressed = true;
                            break;
                        }
                    }
                    // 보균자 출현
                    if (isExpressed
                            && carrierList.size() > 0) { // 보균자가 0명 초과일 경우
                        if (timer == appearanceCarrierTime[i]) {
                            teamChangeCarrier(); // 보균자 팀 변경
                            for (Player player : playerList) {
                                player.sendMessage(serverTitle + " §c§l보균자§f§l가 발현되었습니다!");
                            }
                        }
                        // 타이머 메시지 전송
                        if ((appearanceCarrierTime[i] < timer && timer <= appearanceCarrierTime[i] + 5)
                                || timer - appearanceCarrierTime[i] == 10
                                || timer - appearanceCarrierTime[i] == 20) {
                            for (Player player : playerList) {
                                player.sendMessage(serverTitle + " §c§l보균자 §f§l출현까지 §c§l" + (timer - appearanceCarrierTime[i]) + "초 §f§l남았습니다.");
                            }
                        }
                    }
                }
            }

            // 보급 시스템
            boolean isDropped = false;
            if (supplyEnable) {
                // 보급 투하 시간 구하기
                int[] appearanceSupplyTime = new int[supplyAppearanceCount];
                for(int i = 0; i < supplyAppearanceCount; i++) {
                    appearanceSupplyTime[i] = (int)(roundTime / (supplyAppearanceCount + 1.0)) * (i+1);
                }
                
                for(int i = 0; i < supplyAppearanceCount; i++) {
                    // 보급 투하
                    if (timer == appearanceSupplyTime[i]) {
                        isDropped = supplyDrop(); // 보급 투하 함수 호출
                    }
                }
            }

            // 타이머 메시지 전송
            if (timer % 30 == 0 || timer <= 5) {
                if (!isExpressed && !isDropped) { // 예비된 보균자가 없고, 보급이 투하되지 않았을 경우
                    for (Player player : playerList) {
                        player.sendMessage(serverTitle + " §f§l라운드 종료까지 §c§l" + timer + "초 §f§l남았습니다.");
                    }
                }
            }

        }, 20L, 20L);
    }

    // 종료 함수
    protected void Ending() {
        gameProgress = GameProgress.ENDING; // 게임 진행 상태를 종료로 초기화

        resetSettings(); // 세팅 초기화

        // RoundEndEvent 이벤트 호출
        RoundEndEvent event = new RoundEndEvent(round);
        Bukkit.getServer().getPluginManager().callEvent(event);

        bar.setTitle("");
        bar.setProgress(0);
        plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, () -> {
            bar.setColor(BarColor.WHITE);
        }, 20L);

        timer = 5 + 1;
        task = plugin.getServer().getScheduler().scheduleSyncRepeatingTask(plugin, () -> {
            timer--;
            bar.setProgress((double)timer / (double)5);

            // TimerEvent 이벤트 호출
            TimerEvent timerEvent = new TimerEvent(timer);
            Bukkit.getServer().getPluginManager().callEvent(timerEvent);

            // 강제 중지될 경우
            if (gameProgress == GameProgress.IDLE) {
                Bukkit.getScheduler().cancelTask(task);
                return;
            }

            if (timer == 0) {
                // 라운드 검사
                if (round < roundCount && playerList.size() >= 2) { // 여분의 라운드가 남아 있고, 참여 플레이어 인원이 2명 이상일 경우
                    Bukkit.getScheduler().cancelTask(task); // 태스크 종료
                    Ready(); // 준비 함수 실행
                    return;
                } else { // 여분의 라운드가 남지 않았을 경우
                    Bukkit.getScheduler().cancelTask(task); // 태스크 종료

                    for(Player player : playerList) {
                        restorePlayerData(player); // 플레이어 데이터 복구
                        moveLobby(player); // 로비로 이동
                    }

                    Waiting(); // 대기 함수 실행
                    return;
                }
            }
        }, 20L, 20L);
    }

    // 세팅 초기화 함수
    protected void resetSettings() {
        humanList.clear();
        zombieList.clear();
        hostList.clear();
        livingHumanList.clear();
        heroList.clear();
        carrierList.clear();
        for(String entry : humanTeam.getEntries()) {
            humanTeam.removeEntry(entry);
        }
        for(String entry : zombieTeam.getEntries()) {
            zombieTeam.removeEntry(entry);
        }
        for(Location location : supplyLocation) {
            location.getBlock().setType(Material.AIR);
        }
        humanWin = false;
        zombieWin = false;
    }

    // 맵 선정 함수
    protected void selectMap() {
        serverTitle = Files.getServerTitle();
        lobbyLocation = Files.getLobbyLocation();
        mapList = Files.getMapList();
        if (selectedMapName.equals("")) {
            while (true) {
                if (playingMapList.size() == mapList.size()) { // 플레이 했던 맵 목록 크기와 맵 목록 크기가 같을 경우
                    playingMapList.clear(); // 플레이 했던 맵 목록 초기화
                }
                for (int i = 0; i < (int) (mapList.size() * 50); i++) {
                    mapNumber = (int) (Math.random() * mapList.size()); // 맵 번호 무작위 지정
                    if (!playingMapList.contains(mapNumber)) { // 맵 번호가 플레이 했던 맵 목록에 포함되어 있지 않을 경우
                        break; // 반복 종료
                    }
                }

                int countNumber = 0;
                for (Map.Entry<String, JsonElement> entry : mapList.entrySet()) {
                    if (countNumber == mapNumber) {
                        String key = entry.getKey();
                        JsonElement value = entry.getValue();
                        mapName = key;
                        break;
                    }
                    countNumber++;
                }

                // SelectMapEvent 이벤트 호출
                SelectMapEvent event = new SelectMapEvent(mapName);
                Bukkit.getServer().getPluginManager().callEvent(event);

                if (!event.isCancelled()) { // 이벤트가 취소되지 않을 경우
                    if (!mapName.equals(event.getMapName())) { // 맵 이름이 호출된 이벤트의 맵 이름과 다를 경우
                        int number = 0;
                        for (Map.Entry<String, JsonElement> entry : mapList.entrySet()) {
                            String key = entry.getKey();

                            if (key.equals(event.getMapName())) {
                                break;
                            }
                            number++;
                        }
                        mapNumber = number;
                    }
                    mapName = event.getMapName();

                    if (!playingMapList.contains(mapNumber)) { // 플레이 했던 맵 목록에 맵 번호가 포함되어 있지 않을 경우
                        playingMapList.add(mapNumber); // 맵 번호를 플레이 했던 맵 목록에 추가
                    }

                    break; // 반복 종료
                } else { // 이벤트가 취소됐을 경우
                    if (!playingMapList.contains(mapNumber)) { // 플레이 했던 맵 목록에 맵 번호가 포함되어 있지 않을 경우
                        playingMapList.add(mapNumber); // 맵 번호를 플레이 했던 맵 목록에 추가
                    }
                }
            }
        } else {
            int number = 0;
            for (Map.Entry<String, JsonElement> entry : mapList.entrySet()) {
                String key = entry.getKey();

                if (key.equals(selectedMapName)) {
                    break;
                }
                number++;
            }
            mapNumber = number;
            mapName = selectedMapName;

            if (!playingMapList.contains(mapNumber)) { // 플레이 했던 맵 목록에 맵 번호가 포함되어 있지 않을 경우
                playingMapList.add(mapNumber); // 맵 번호를 플레이 했던 맵 목록에 추가
            }
        }
        selectedMapName = "";
        mapLocation = Files.getMapLocation(mapName);
        restTime = Files.getMapRestTime(mapName) == -1 ? Files.getRestTime() : Files.getMapRestTime(mapName);
        readyTime = Files.getMapReadyTime(mapName) == -1 ? Files.getReadyTime() : Files.getMapReadyTime(mapName);
        roundTime = Files.getMapRoundTime(mapName) == -1 ? Files.getRoundTime() : Files.getMapRoundTime(mapName);
        roundCount = Files.getMapRoundCount(mapName) == -1 ? Files.getRoundCount() : Files.getMapRoundCount(mapName);
        hostStartCount = Files.getMapHostStartCount(mapName) == -1 ? Files.getHostStartCount() : Files.getMapHostStartCount(mapName);
        hostIntervalCount = Files.getMapHostIntervalCount(mapName) == -1 ? Files.getHostIntervalCount() : Files.getMapHostIntervalCount(mapName);
        hostMaxCount = Files.getMapHostMaxCount(mapName) == -1 ? Files.getHostMaxCount() : Files.getMapHostMaxCount(mapName);
        heroEnable = Files.getMapHeroEnable(mapName);
        heroStartCount = Files.getMapHeroStartCount(mapName) == -1 ? Files.getHeroStartCount() : Files.getMapHeroStartCount(mapName);
        heroIntervalCount = Files.getMapHeroIntervalCount(mapName) == -1 ? Files.getHeroIntervalCount() : Files.getMapHeroIntervalCount(mapName);
        heroMaxCount = Files.getMapHeroMaxCount(mapName) == -1 ? Files.getHeroMaxCount() : Files.getMapHeroMaxCount(mapName);
        carrierEnable = Files.getMapCarrierEnable(mapName);
        carrierMinCount = Files.getMapCarrierMinCount(mapName) == -1 ? Files.getCarrierMinCount() : Files.getMapCarrierMinCount(mapName);
        carrierAppearanceCount = Files.getMapCarrierAppearanceCount(mapName) == -1 ? Files.getCarrierAppearanceCount() : Files.getMapCarrierAppearanceCount(mapName);
        carrierRatio = Files.getMapCarrierRatio(mapName) == -1 ? Files.getCarrierRatio() : Files.getMapCarrierRatio(mapName);
        supplyEnable = Files.getMapSupplyEnable(mapName);
        supplyAppearanceCount = Files.getMapSupplyAppearanceCount(mapName) == -1 ? Files.getSupplyAppearanceCount() : Files.getMapSupplyAppearanceCount(mapName);
        supplyLocation = Files.getMapSupplyLocation(mapName);
        commandConvertDefault = Files.getMapCommandConvertDefault(mapName).equals("default") ? Files.getCommandConvertDefault() : Files.getMapCommandConvertDefault(mapName);
        commandConvertHuman = Files.getMapCommandConvertHuman(mapName).equals("default") ? Files.getCommandConvertHuman() : Files.getMapCommandConvertHuman(mapName);
        commandConvertZombie = Files.getMapCommandConvertZombie(mapName).equals("default") ? Files.getCommandConvertZombie() : Files.getMapCommandConvertZombie(mapName);
        commandConvertHost = Files.getMapCommandConvertHost(mapName).equals("default") ? Files.getCommandConvertHost() : Files.getMapCommandConvertHost(mapName);
        commandConvertHero = Files.getMapCommandConvertHero(mapName).equals("default") ? Files.getCommandConvertHero() : Files.getMapCommandConvertHero(mapName);
        commandConvertCarrier = Files.getMapCommandConvertCarrier(mapName).equals("default") ? Files.getCommandConvertCarrier() : Files.getMapCommandConvertCarrier(mapName);
        commandAcquiredSupply = Files.getMapCommandAcquiredSupply(mapName).equals("default") ? Files.getCommandAcquiredSupply() : Files.getMapCommandAcquiredSupply(mapName);
        commandRewardWin = Files.getMapCommandRewardWin(mapName).equals("default") ? Files.getCommandRewardWin() : Files.getMapCommandRewardWin(mapName);
        commandRewardLose = Files.getMapCommandRewardLose(mapName).equals("default") ? Files.getCommandRewardLose() : Files.getMapCommandRewardLose(mapName);
        commandRewardKill = Files.getMapCommandRewardKill(mapName).equals("default") ? Files.getCommandRewardKill() : Files.getMapCommandRewardKill(mapName);
        commandRewardCure = Files.getMapCommandRewardCure(mapName).equals("default") ? Files.getCommandRewardCure() : Files.getMapCommandRewardCure(mapName);
        commandRewardDeath = Files.getMapCommandRewardDeath(mapName).equals("default") ? Files.getCommandRewardDeath() : Files.getMapCommandRewardDeath(mapName);
        commandEscapePenalty = Files.getMapCommandEscapePenalty(mapName).equals("default") ? Files.getCommandEscapePenalty() : Files.getMapCommandEscapePenalty(mapName);
    }

    // 숙주 선정 함수
    protected void selectHost() {
        int humanCount = humanList.size();
        // 인간 인원 수가 0명일 경우
        if (humanCount == 0) {
            return;
        }

        // 숙주 인원 수 결정
        int hostCount = 0;
        // 인간 인원 수가 숙주 시작 인원 수보다 적을 경우
        if (humanCount < hostStartCount) {
            hostCount = 1;
        }
        // 인간 인원 수가 숙주 시작 인원 수보다 같거나 많을 경우
        else {
            hostCount = (humanCount - hostStartCount) / hostIntervalCount + 2;
            hostCount = hostCount > hostMaxCount ? hostMaxCount : hostCount;
        }

        // SelectHostEvent 호출
        SelectHostEvent selectHostEvent = new SelectHostEvent(humanList);
        Bukkit.getPluginManager().callEvent(selectHostEvent);

        ArrayList<Player> sHumanList = selectHostEvent.getHumanList();
        int sHumanCount = sHumanList.size();

        // 숙주 플레이어 선정
        for (int n = 0, c = 0; n < hostCount && c < 1000; c++)
        {
            int randomNumber = (int)(Math.random() * sHumanCount);
            Player player = sHumanList.get(randomNumber);
            
            // 플레이어가 이미 숙주 목록에 포함되어 있을 경우
            if (hostList.contains(player)) {
                continue;
            }
            
            // 플레이어가 과거에 숙주 목록에 포함되어 있었을 경우
            if (wasHostList.contains(player)) {
                wasHostList.remove(player); continue;
            }
            wasHostList.add(player); n++;

            // 플레이어를 좀비 및 숙주 목록에 추가
            zombieList.add(player);
            zombieTeam.addEntry(player.getName());
            hostList.add(player);

            // 플레이어를 인간 목록에서 제거
            humanList.remove(player);
            humanTeam.removeEntry(player.getName());
        }
    }

    // 영웅 선정 함수
    protected void selectHero() {
        int humanCount = humanList.size();
        if (humanCount == 0) { // 인간 인원 수가 0명일 경우
            return;
        }

        // 영웅 인원 수 결정
        int playerCount = playerList.size();
        int heroCount = 0;
        if (playerCount >= heroStartCount) { // 인간 인원 수가 영웅 시작 인원 수보다 같거나 많을 경우
            heroCount = (playerCount - heroStartCount) / heroIntervalCount + 1;
            heroCount = heroCount > heroMaxCount ? heroMaxCount : heroCount;
        }

        // 영웅 플레이어 선정
        ArrayList<Integer> heroNumberList = new ArrayList<Integer>();
        for (int n = 0, c = 0; n < heroCount && c < 100; n++, c++)
        {
            int randomNumber = (int)(Math.random() * humanCount);
            if (heroNumberList.contains(randomNumber)) { n--; continue; }
            if (wasHeroList.contains(humanList.get(randomNumber))) { wasHeroList.remove(humanList.get(randomNumber)); n--; continue; }
            heroNumberList.add(randomNumber);
            wasHeroList.add(humanList.get(randomNumber));
        }

        for (int number : heroNumberList)
        {
            Player player = humanList.get(number);
            heroList.add(player);
        }
    }

    // 보균자 선정 함수
    protected Player selectCarrier() {
        int humanCount = humanList.size();
        int zombieCount = zombieList.size();

        // SelectCarrierEvent 호출
        SelectCarrierEvent selectCarrierEvent = new SelectCarrierEvent(humanList);
        Bukkit.getPluginManager().callEvent(selectCarrierEvent);

        ArrayList<Player> sHumanList = selectCarrierEvent.getHumanList();
        int sHumanCount = sHumanList.size();

        // 인간 인원 수가 좀비 수보다 보균자 출현에 필요한 인간과 좀비 비율보다 많을 경우
        if (humanCount >= zombieCount * carrierRatio) {
            // 생존자 중에서 보균자 플레이어 선정
            for (int i = 0; i < 1000; i++) {
                int randomNumber = (int) (Math.random() * sHumanCount);
                Player player = sHumanList.get(randomNumber);

                // 플레이어가 이미 보균자 목록에 포함되어 있을 경우
                if (carrierList.contains(player)) {
                    continue;
                }

                // 플레이어가 영웅일 경우
                if (heroList.contains(player)) {
                    continue;
                }

                // 플레이어가 과거에 숙주 목록에 포함되어 있었을 경우
                if (wasHostList.contains(player)) {
                    wasHostList.remove(player); continue;
                }
                wasHostList.add(player);

                carrierList.add(player);
                return player;
            }
        }

        // 인간 인원 수가 좀비 수보다 보균자 출현에 필요한 인간과 좀비 비율보다 많을 경우
        else {
            // 감염자 중에서 보균자 플레이어 선정
            for (int i = 0; i < 1000; i++) {
                int randomNumber = (int) (Math.random() * zombieCount);
                Player player = zombieList.get(randomNumber);

                // 플레이어가 숙주 혹은 보균자일 경우
                if (hostList.contains(player)
                        || carrierList.contains(player)) {
                    continue;
                }

                carrierList.add(player);
                return player;
            }
        }

        return null;
    }

    // 숙주 팀 변경 함수
    protected void teamChangeHost() {
        for(Player player : hostList) {

            // TeamChangeHostEvent 호출
            TeamChangeHostEvent event = new TeamChangeHostEvent(player);
            Bukkit.getServer().getPluginManager().callEvent(event);

            convertZombie(player); // 좀비 변환 함수 호출
            convertHost(player); // 숙주 변환 함수 호출
            player.sendTitle("§c§l숙주", "§f모든 §9인간§f을 감염시키십시오!", 5, 100, 5);
        }
    }

    // 보균자 팀 변경 함수
    protected void teamChangeCarrier() {
        for(Player player : carrierList) {
            if (!zombieList.contains(player)) { // 플레이어가 좀비가 아닐 경우

                // TeamChangeCarrierEvent 호출
                TeamChangeCarrierEvent event = new TeamChangeCarrierEvent(player);
                Bukkit.getServer().getPluginManager().callEvent(event);

                humanList.remove(player);
                humanTeam.removeEntry(player.getName());
                if (heroList.contains(player)) {
                    heroList.remove(player);
                }
                zombieList.add(player);
                zombieTeam.addEntry(player.getName());

                convertZombie(player); // 좀비 변환 함수 호출
                convertCarrier(player); // 보균자 변환 함수 호출
                player.sendTitle("§c§l보균자", "§f모든 §9인간§f을 감염시키십시오!", 5, 100, 5);
            }
        }
    }

    // 감염자를 보균자로 변경 함수
    protected void infecteeChangeCarrier(Player carrier) {
        // InfecteeChangeCarrierEvent 호출
        InfecteeChangeCarrierEvent event = new InfecteeChangeCarrierEvent(carrier);
        Bukkit.getServer().getPluginManager().callEvent(event);

        convertZombie(carrier); // 좀비 변환 함수 호출
        convertCarrier(carrier); // 보균자 변환 함수 호출
        carrier.sendTitle("§c§l보균자", "§f모든 §9인간§f을 감염시키십시오!", 5, 100, 5);
    }

    // 보급 투하 함수
    public boolean supplyDrop() {
        int randomNumber = 0;
        for(int i = 0; i < 100; i++) {
            randomNumber = (int)(Math.random() * supplyLocation.size());
            if (supplyLocation.get(randomNumber).getBlock().getType() != Material.BEACON) {
                break;
            }
        }

        // SupplyDropEvent 이벤트 호출
        SupplyDropEvent event = new SupplyDropEvent(supplyLocation.get(randomNumber));
        Bukkit.getServer().getPluginManager().callEvent(event);

        if (!event.isCancelled()) {
            event.getLocation().getWorld().strikeLightningEffect(event.getLocation());
            event.getLocation().getBlock().setType(Material.BEACON);

            for (Player player : playerList) {
                player.sendMessage(serverTitle + " §9§l보급§f§l이 투하되었습니다!");
            }
        }

        return !event.isCancelled();
    }

    // 플레이어 데이터 백업 함수
    public void backupPlayerData(Player player) {
        // BackupPlayerDataEvent 호출
        BackupPlayerDataEvent event = new BackupPlayerDataEvent(player);
        Bukkit.getServer().getPluginManager().callEvent(event);

        if (!event.isCancelled()) { // 이벤트가 취소되지 않았을 경우
            itemMap.put(event.getPlayer(), new ArrayList<ItemStack>()); // 인벤토리 백업
            for (int i = 0; i < 41; i++) {
                itemMap.get(event.getPlayer()).add(event.getPlayer().getInventory().getItem(i));
            }
            modeMap.put(event.getPlayer(), event.getPlayer().getGameMode()); // 게임 모드 백업
            foodLevelMap.put(event.getPlayer(), event.getPlayer().getFoodLevel()); // 허기 백업
        }
    }

    // 플레이어 데이터 복구 함수
    public void restorePlayerData(Player player) {
        // RestorePlayerDataEvent 호출
        RestorePlayerDataEvent event = new RestorePlayerDataEvent(player);
        Bukkit.getServer().getPluginManager().callEvent(event);

        if (!event.isCancelled()) { // 이벤트가 취소되지 않았을 경우
            for (int i = 0; i < 41; i++) { // 인벤토리 복구
                event.getPlayer().getInventory().setItem(i, itemMap.get(event.getPlayer()).get(i));
            }
            event.getPlayer().setGameMode(modeMap.get(event.getPlayer())); // 게임 모드 복구
            event.getPlayer().setFoodLevel(foodLevelMap.get(event.getPlayer())); // 허기 복구
        }
    }

    // 기본 변환 함수
    public void convertDefault(Player player) {
        // ConvertDefaultEvent 이벤트 호출
        ConvertDefaultEvent event = new ConvertDefaultEvent(player);
        Bukkit.getServer().getPluginManager().callEvent(event);

        if (!event.isCancelled()) { // 이벤트가 취소되지 않았을 경우
            for(int i = 0; i < 41; i++) {
                event.getPlayer().getInventory().setItem(i, new ItemStack(Material.AIR));
            }
            event.getPlayer().setGameMode(GameMode.ADVENTURE);
            event.getPlayer().setFoodLevel(20);
            event.getPlayer().setHealth(event.getPlayer().getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue()); // 체력 초기화
            if (!commandConvertDefault.equals("")) { // 명령어 변수가 비어있지 않을 경우
                if (event.getPlayer().isOp()) { // 플레이어가 오피일 경우
                    Bukkit.dispatchCommand(event.getPlayer(), commandConvertDefault); // 명령어 실행
                } else { // 플레이어가 오피가 아닐 경우
                    event.getPlayer().setOp(true); // 오피 권환 지급
                    Bukkit.dispatchCommand(event.getPlayer(), commandConvertDefault); // 명령어 실행
                    event.getPlayer().setOp(false); // 오피 권환 회수
                }
            }
        }
    }

    // 인간 변환 함수
    public void convertHuman(Player player) {
        // ConvertHumanEvent 이벤트 호출
        ConvertHumanEvent event = new ConvertHumanEvent(player);
        Bukkit.getServer().getPluginManager().callEvent(event);

        if (!event.isCancelled()) { // 이벤트가 취소되지 않았을 경우
            for(int i = 0; i < 41; i++) {
                event.getPlayer().getInventory().setItem(i, new ItemStack(Material.AIR));
            }
            event.getPlayer().getInventory().setHelmet(new ItemStack(Material.AIR));
            if (!commandConvertHuman.equals("")) { // 명령어 변수가 비어있지 않을 경우
                if (event.getPlayer().isOp()) { // 플레이어가 오피일 경우
                    Bukkit.dispatchCommand(event.getPlayer(), commandConvertHuman); // 명령어 실행
                } else { // 플레이어가 오피가 아닐 경우
                    event.getPlayer().setOp(true); // 오피 권환 지급
                    Bukkit.dispatchCommand(event.getPlayer(), commandConvertHuman); // 명령어 실행
                    event.getPlayer().setOp(false); // 오피 권환 회수
                }
            }
        }
    }

    // 좀비 변환 함수
    public void convertZombie(Player player) {
        // ConvertZombieEvent 이벤트 호출
        ConvertZombieEvent event = new ConvertZombieEvent(player);
        Bukkit.getServer().getPluginManager().callEvent(event);

        if (!event.isCancelled()) { // 이벤트가 취소되지 않았을 경우
            for(int i = 0; i < 41; i++) {
                event.getPlayer().getInventory().setItem(i, new ItemStack(Material.AIR));
            }
            event.getPlayer().getInventory().setHelmet(new ItemStack(Material.JACK_O_LANTERN));
            if (!commandConvertZombie.equals("")) { // 명령어 변수가 비어있지 않을 경우
                if (event.getPlayer().isOp()) { // 플레이어가 오피일 경우
                    Bukkit.dispatchCommand(event.getPlayer(), commandConvertZombie); // 명령어 실행
                } else { // 플레이어가 오피가 아닐 경우
                    event.getPlayer().setOp(true); // 오피 권환 지급
                    Bukkit.dispatchCommand(event.getPlayer(), commandConvertZombie); // 명령어 실행
                    event.getPlayer().setOp(false); // 오피 권환 회수
                }
            }
        }
    }

    // 숙주 변환 함수
    public void convertHost(Player player) {
        // ConvertHostEvent 이벤트 호출
        ConvertHostEvent event = new ConvertHostEvent(player);
        Bukkit.getServer().getPluginManager().callEvent(event);

        if (!event.isCancelled()) { // 이벤트가 취소되지 않았을 경우
            if (!commandConvertHost.equals("")) { // 명령어 변수가 비어있지 않을 경우
                if (event.getPlayer().isOp()) { // 플레이어가 오피일 경우
                    Bukkit.dispatchCommand(event.getPlayer(), commandConvertHost); // 명령어 실행
                } else { // 플레이어가 오피가 아닐 경우
                    event.getPlayer().setOp(true); // 오피 권환 지급
                    Bukkit.dispatchCommand(event.getPlayer(), commandConvertHost); // 명령어 실행
                    event.getPlayer().setOp(false); // 오피 권환 회수
                }
            }
        }
    }

    // 영웅 변환 함수
    public void convertHero(Player player) {
        // ConvertHeroEvent 이벤트 호출
        ConvertHeroEvent event = new ConvertHeroEvent(player);
        Bukkit.getServer().getPluginManager().callEvent(event);

        if (!event.isCancelled()) { // 이벤트가 취소되지 않았을 경우
            if (!commandConvertHero.equals("")) { // 명령어 변수가 비어있지 않을 경우
                if (event.getPlayer().isOp()) { // 플레이어가 오피일 경우
                    Bukkit.dispatchCommand(event.getPlayer(), commandConvertHero); // 명령어 실행
                } else { // 플레이어가 오피가 아닐 경우
                    event.getPlayer().setOp(true); // 오피 권환 지급
                    Bukkit.dispatchCommand(event.getPlayer(), commandConvertHero); // 명령어 실행
                    event.getPlayer().setOp(false); // 오피 권환 회수
                }
            }
        }
    }

    // 보균자 변환 함수
    public void convertCarrier(Player player) {
        // ConvertCarrierEvent 이벤트 호출
        ConvertCarrierEvent event = new ConvertCarrierEvent(player);
        Bukkit.getServer().getPluginManager().callEvent(event);

        if (!event.isCancelled()) { // 이벤트가 취소되지 않았을 경우
            if (!commandConvertCarrier.equals("")) { // 명령어 변수가 비어있지 않을 경우
                if (event.getPlayer().isOp()) { // 플레이어가 오피일 경우
                    Bukkit.dispatchCommand(event.getPlayer(), commandConvertCarrier); // 명령어 실행
                } else { // 플레이어가 오피가 아닐 경우
                    event.getPlayer().setOp(true); // 오피 권환 지급
                    Bukkit.dispatchCommand(event.getPlayer(), commandConvertCarrier); // 명령어 실행
                    event.getPlayer().setOp(false); // 오피 권환 회수
                }
            }
        }
    }

    // 보급 변환 함수
    public void acquiredSupply(Player player, Block block) {
        // AcquiredSupplyEvent 이벤트 호출
        SupplyAcquireEvent event = new SupplyAcquireEvent(player, block);
        Bukkit.getServer().getPluginManager().callEvent(event);

        if (!event.isCancelled()) { // 이벤트가 취소되지 않았을 경우
            event.getBlock().setType(Material.AIR); // 보급 블럭 제거
            if (!commandAcquiredSupply.equals("")) { // 명령어 변수가 비어있지 않을 경우
                if (event.getPlayer().isOp()) { // 플레이어가 오피일 경우
                    Bukkit.dispatchCommand(event.getPlayer(), commandAcquiredSupply); // 명령어 실행
                } else { // 플레이어가 오피가 아닐 경우
                    event.getPlayer().setOp(true); // 오피 권환 지급
                    Bukkit.dispatchCommand(event.getPlayer(), commandAcquiredSupply); // 명령어 실행
                    event.getPlayer().setOp(false); // 오피 권환 회수
                }
            }
        }
    }

    // 승리 보상 함수
    public void rewardWin(Player player) {
        // RewardWinEvent 이벤트 호출
        RewardWinEvent event = new RewardWinEvent(player);
        Bukkit.getServer().getPluginManager().callEvent(event);

        if (!event.isCancelled()) { // 이벤트가 취소되지 않았을 경우
            if (!commandRewardWin.equals("")) { // 명령어 변수가 비어있지 않을 경우
                if (event.getPlayer().isOp()) { // 플레이어가 오피일 경우
                    Bukkit.dispatchCommand(event.getPlayer(), commandRewardWin); // 명령어 실행
                } else { // 플레이어가 오피가 아닐 경우
                    event.getPlayer().setOp(true); // 오피 권환 지급
                    Bukkit.dispatchCommand(event.getPlayer(), commandRewardWin); // 명령어 실행
                    event.getPlayer().setOp(false); // 오피 권환 회수
                }
            }
        }
    }

    // 패배 보상 함수
    public void rewardLose(Player player) {
        // RewardLoseEvent 이벤트 호출
        RewardLoseEvent event = new RewardLoseEvent(player);
        Bukkit.getServer().getPluginManager().callEvent(event);

        if (!event.isCancelled()) { // 이벤트가 취소되지 않았을 경우
            if (!commandRewardLose.equals("")) { // 명령어 변수가 비어있지 않을 경우
                if (event.getPlayer().isOp()) { // 플레이어가 오피일 경우
                    Bukkit.dispatchCommand(event.getPlayer(), commandRewardLose); // 명령어 실행
                } else { // 플레이어가 오피가 아닐 경우
                    event.getPlayer().setOp(true); // 오피 권환 지급
                    Bukkit.dispatchCommand(event.getPlayer(), commandRewardLose); // 명령어 실행
                    event.getPlayer().setOp(false); // 오피 권환 회수
                }
            }
        }
    }

    // 킬 보상 함수
    public void rewardKill(Player player) {
        // RewardKillEvent 이벤트 호출
        RewardKillEvent event = new RewardKillEvent(player);
        Bukkit.getServer().getPluginManager().callEvent(event);

        if (!event.isCancelled()) { // 이벤트가 취소되지 않았을 경우
            if (!commandRewardKill.equals("")) { // 명령어 변수가 비어있지 않을 경우
                if (event.getPlayer().isOp()) { // 플레이어가 오피일 경우
                    Bukkit.dispatchCommand(event.getPlayer(), commandRewardKill); // 명령어 실행
                } else { // 플레이어가 오피가 아닐 경우
                    event.getPlayer().setOp(true); // 오피 권환 지급
                    Bukkit.dispatchCommand(event.getPlayer(), commandRewardKill); // 명령어 실행
                    event.getPlayer().setOp(false); // 오피 권환 회수
                }
            }
        }
    }

    // 치유 보상 함수
    public void rewardCure(Player player) {
        // RewardCureEvent 이벤트 호출
        RewardCureEvent event = new RewardCureEvent(player);
        Bukkit.getServer().getPluginManager().callEvent(event);

        if (!event.isCancelled()) { // 이벤트가 취소되지 않았을 경우
            if (!commandRewardCure.equals("")) { // 명령어 변수가 비어있지 않을 경우
                if (event.getPlayer().isOp()) { // 플레이어가 오피일 경우
                    Bukkit.dispatchCommand(event.getPlayer(), commandRewardCure); // 명령어 실행
                } else { // 플레이어가 오피가 아닐 경우
                    event.getPlayer().setOp(true); // 오피 권환 지급
                    Bukkit.dispatchCommand(event.getPlayer(), commandRewardCure); // 명령어 실행
                    event.getPlayer().setOp(false); // 오피 권환 회수
                }
            }
        }
    }

    // 데스 보상 함수
    public void rewardDeath(Player player) {
        // RewardDeathEvent 이벤트 호출
        RewardDeathEvent event = new RewardDeathEvent(player);
        Bukkit.getServer().getPluginManager().callEvent(event);

        if (!event.isCancelled()) { // 이벤트가 취소되지 않았을 경우
            if (!commandRewardDeath.equals("")) { // 명령어 변수가 비어있지 않을 경우
                if (event.getPlayer().isOp()) { // 플레이어가 오피일 경우
                    Bukkit.dispatchCommand(event.getPlayer(), commandRewardDeath); // 명령어 실행
                } else { // 플레이어가 오피가 아닐 경우
                    event.getPlayer().setOp(true); // 오피 권환 지급
                    Bukkit.dispatchCommand(event.getPlayer(), commandRewardDeath); // 명령어 실행
                    event.getPlayer().setOp(false); // 오피 권환 회수
                }
            }
        }
    }

    // 탈주 패널티 함수
    public void escapePenalty(Player player) {
        // EscapePenaltyEvent 이벤트 호출
        EscapePenaltyEvent event = new EscapePenaltyEvent(player);
        Bukkit.getServer().getPluginManager().callEvent(event);

        if (!event.isCancelled()) { // 이벤트가 취소되지 않았을 경우
            if (!commandEscapePenalty.equals("")) { // 명령어 변수가 비어있지 않을 경우
                if (event.getPlayer().isOp()) { // 플레이어가 오피일 경우
                    Bukkit.dispatchCommand(event.getPlayer(), commandEscapePenalty); // 명령어 실행
                } else { // 플레이어가 오피가 아닐 경우
                    event.getPlayer().setOp(true); // 오피 권환 지급
                    Bukkit.dispatchCommand(event.getPlayer(), commandEscapePenalty); // 명령어 실행
                    event.getPlayer().setOp(false); // 오피 권환 회수
                }
            }
        }
    }


    // 게임 시작 함수
    public boolean gameStart() {
        if (gameProgress == GameProgress.IDLE) { // 게임 진행 상태가 운휴 중일 경우
            // GameStartEvent 이벤트 호출
            GameStartEvent event = new GameStartEvent();
            Bukkit.getServer().getPluginManager().callEvent(event);

            bar.setVisible(true);

            Waiting(); // 대기 함수 호출
            return true;
        } else { // 게임 진행 상태가 운휴 중이 아닐 경우
            return false;
        }
    }

    // 게임 중지 함수
    public boolean gameStop() {
        if (gameProgress != GameProgress.IDLE) { // 게임 진행 상태가 운휴 중이 아닐 경우
            // GameStopEvent 이벤트 호출
            GameStopEvent event = new GameStopEvent();
            Bukkit.getServer().getPluginManager().callEvent(event);

            switch(gameProgress) {
                case READY: // 준비 중일 경우
                    for(Player player : playerList) {
                        if (humanList.contains(player)) { // 플레이어가 인간일 경우
                            humanList.remove(player); // 인간 목록에서 플레이어 제거
                            humanTeam.removeEntry(player.getName()); // 인간 팀에서 플레이어 제거
                        }
                        convertDefault(player); // 기본 변환 함수 호출
                        restorePlayerData(player); // 플레이어 데이터 복구
                        moveLobby(player); // 로비로 이동
                    }
                    resetSettings(); // 세팅 초기화
                    break;
                case GAME: // 게임 중일 경우
                    for(Player player : playerList) {
                        if (humanList.contains(player)) { // 플레이어가 인간일 경우
                            if (livingHumanList.contains(player)) { // 플레이어가 생존한 인간일 경우
                                livingHumanList.remove(player); // 생존한 플레이어 목록에서 플레이어 제거
                            }
                            if (heroList.contains(player)) { // 플레이어가 영웅일 경우
                                heroList.remove(player); // 영웅 목록에서 플레이어 제거
                            }
                            if (carrierList.contains(player)) { // 플레이어가 보균자일 경우
                                carrierList.remove(player); // 보균자 목록에서 플레이어 제거
                            }
                            humanList.remove(player); // 인간 목록에서 플레이어 제거
                            humanTeam.removeEntry(player.getName()); // 인간 팀에서 플레이어 제거
                        } else if (zombieList.contains(player)) { // 플레이어가 좀비일 경우
                            if (hostList.contains(player)) { // 플레이어가 숙주일 경우
                                hostList.remove(player); // 숙주 목록에서 플레이어 제거
                            }
                            zombieList.remove(player); // 좀비 목록에서 플레이어 제거
                            zombieTeam.removeEntry(player.getName()); // 좀비 팀에서 플레이어 제거
                        }
                        convertDefault(player); // 기본 변환 함수 호출
                        restorePlayerData(player); // 플레이어 데이터 복구
                        moveLobby(player); // 로비로 이동
                    }
                    resetSettings(); // 세팅 초기화
                    break;
                case ENDING: // 종료 중일 경우
                    for(Player player : playerList) {
                        restorePlayerData(player); // 플레이어 데이터 복구
                        moveLobby(player); // 로비로 이동
                    }
                    break;
            }
            bar.setVisible(false);

            gameProgress = GameProgress.IDLE; // 게임 진행 상태를 운휴로 변경
            return true;
        } else { // 게임 진행 상태가 운휴 중일 경우
            return false;
        }
    }

    // 참여 플레이어 목록 초기화
    public void initPlayerList() {
        for(Player player : (ArrayList<Player>)playerList.clone()) {
            removePlayer(player);
        }
    }

    // 플레이어 추가 함수
    public boolean addPlayer(Player player) {
        if (!playerList.contains((player))) { // 게임 참여 플레이어 명단에 플레이어가 없을 경우
            // PlayerAddEvent 이벤트 호출
            PlayerAddEvent event = new PlayerAddEvent(player, gameProgress);
            Bukkit.getServer().getPluginManager().callEvent(event);

            if (!event.isCancelled()) { // 이벤트가 취소되지 않았을 경우
                playerList.add(event.getPlayer()); // 게임 참여 플레이어 명단에 플레이어 추가
                switch (event.getGameProgress()) {
                    case READY:
                        humanList.add(event.getPlayer()); // 인간 목록에 플레이어 추가
                        humanTeam.addEntry(event.getPlayer().getName()); // 인간 팀에 플레이어 추가
                        backupPlayerData(event.getPlayer()); // 플레이어 데이터 백업
                        convertDefault(player); // 기본 변환
                        convertHuman(event.getPlayer()); // 인간으로 변환
                        moveMap(event.getPlayer()); // 맵으로 이동
                        break;
                    case GAME:
                        zombieList.add(event.getPlayer()); // 좀비 목록에 플레이어 추가
                        zombieTeam.addEntry(event.getPlayer().getName()); // 좀비 팀에 플레이어 추가
                        backupPlayerData(event.getPlayer()); // 플레이어 데이터 백업
                        convertDefault(player); // 기본 변환
                        convertZombie(event.getPlayer()); // 좀비로 변환
                        moveMap(event.getPlayer()); // 맵으로 이동
                        break;
                    case ENDING:
                        backupPlayerData(event.getPlayer()); // 플레이어 데이터 백업
                        convertDefault(event.getPlayer()); // 기본으로 변환
                        moveMap(event.getPlayer());
                        break;
                }
                bar.addPlayer(event.getPlayer()); // 보스바 추가
            }
            return true;
        } else { // 게임 참여 플레이어 명단에 플레이어가 이미 있을 경우
            return false;
        }
    }

    // 플레이어 제거 함수
    public boolean removePlayer(Player player) {
        if (playerList.contains((player))) { // 게임 참여 플레이어 명단에 플레이어가 있을 경우
            // PlayerRemoveEvent 이벤트 호출
            PlayerRemoveEvent event = new PlayerRemoveEvent(player, gameProgress);
            Bukkit.getServer().getPluginManager().callEvent(event);
            
            if (!event.isCancelled()) { // 이벤트가 취소되지 않았을 경우
                if (humanList.contains(event.getPlayer())) { // 플레이어가 인간일 경우
                    if (livingHumanList.contains(event.getPlayer())) { // 플레이어가 생존한 인간일 경우
                        livingHumanList.remove(event.getPlayer()); // 생존한 플레이어 목록에서 플레이어 제거
                    }
                    if (heroList.contains(event.getPlayer())) { // 플레이어가 영웅일 경우
                        heroList.remove(event.getPlayer()); // 영웅 목록에서 플레이어 제거
                    }
                    if (carrierList.contains(event.getPlayer())) { // 플레이어가 보균자일 경우
                        carrierList.remove(event.getPlayer()); // 보균자 목록에서 플레이어 제거
                    }
                    humanList.remove(event.getPlayer()); // 인간 목록에서 플레이어 제거
                    humanTeam.removeEntry(event.getPlayer().getName()); // 인간 팀에서 플레이어 제거
                } else if (zombieList.contains(event.getPlayer())) { // 플레이어가 좀비일 경우
                    if (hostList.contains(event.getPlayer())) { // 플레이어가 숙주일 경우
                        hostList.remove(event.getPlayer()); // 숙주 목록에서 플레이어 제거
                    }
                    zombieList.remove(event.getPlayer()); // 좀비 목록에서 플레이어 제거
                    zombieTeam.removeEntry(event.getPlayer().getName()); // 좀비 팀에서 플레이어 제거
                }
                playerList.remove(event.getPlayer()); // 게임 참여 플레이어 목록에서 플레이어 제거
                bar.removePlayer(event.getPlayer()); // 보스바 제거
                // 게임 진행 상태가 준비 중 혹은 게임 중 혹은 종료 중일 경우
                GameProgress gameProgress = event.getGameProgress();
                if (gameProgress == GameProgress.READY || gameProgress == GameProgress.GAME || gameProgress == GameProgress.ENDING) {
                    convertDefault(event.getPlayer()); // 기본 변환 함수 호출
                    restorePlayerData(event.getPlayer()); // 플레이어 데이터 복구
                    moveLobby(event.getPlayer()); // 로비로 이동
                }
            }
            return true;
        } else { // 게임 참여 플레이어 명단에 플레이어가 이미 없을 경우
            return false;
        }
    }

    // 맵 이동 함수
    public void moveMap(Player player) {
        // PlayerMoveMapEvent 이벤트 호출
        PlayerMoveMapEvent event = new PlayerMoveMapEvent(player, mapLocation);
        Bukkit.getServer().getPluginManager().callEvent(event);

        if (!event.isCancelled()) { // 이벤트가 취소되지 않았을 경우
            int randomNumber = (int) (Math.random() * event.getMapLocation().size());
            event.getPlayer().teleport(event.getMapLocation().get(randomNumber)); // 맵으로 이동
        }
    }

    // 로비 이동 함수
    public void moveLobby(Player player) {
        // PlayerMoveLobbyEvent 이벤트 호출
        PlayerMoveLobbyEvent event = new PlayerMoveLobbyEvent(player, lobbyLocation);
        Bukkit.getServer().getPluginManager().callEvent(event);

        if (!event.isCancelled()) { // 이벤트가 취소되지 않았을 경우
            event.getPlayer().teleport(event.getLobbyLocation()); // 로비로 이동
        }
    }

    // 감염 함수
    public boolean infected(Player human) {
        if (gameProgress == GameProgress.GAME) { // 게임 진행 상태가 게임 중일 경우
            if (!zombieList.contains(human)) { // 플레이어가 좀비가 아닐 경우
                // HumanInfectedEvent 이벤트 호출
                HumanInfectedEvent event = new HumanInfectedEvent(human);
                Bukkit.getServer().getPluginManager().callEvent(event);

                if (!event.isCancelled()) { // 이벤트가 취소되지 않았을 경우
                    if (humanList.contains(human)) { // 플레이어가 인간일 경우
                        if (livingHumanList.contains(human)) { // 플레이어가 생존한 인간일 경우
                            livingHumanList.remove(human); // 생존한 플레이어 목록에서 플레이어 제거
                        }
                        if (heroList.contains(human)) { // 플레이어가 영웅일 경우
                            heroList.remove(human); // 영웅 목록에서 플레이어 제거
                        }
                        humanList.remove(human); // 인간 목록에서 플레이어 제거
                        humanTeam.removeEntry(human.getName()); // 인간 팀에서 플레이어 제거
                    }
                    zombieList.add(human); // 좀비 목록에 플레이어 추가
                    zombieTeam.addEntry(human.getName()); // 좀비 팀에 플레이어 추가
                    convertZombie(human); // 좀비 변환 함수 호출
                    if (carrierList.contains(human)) { // 플레이어가 보균자일 경우
                        convertCarrier(human); // 보균자 변환 함수 호출
                        human.sendTitle("§c§l보균자", "§f모든 §9인간§f을 감염시키십시오!", 5, 100, 5);
                    } else { // 플레이어가 보균자가 아닐 경우
                        human.sendTitle("§c§l감염자", "§f모든 §9인간§f을 감염시키십시오!", 5, 100, 5);
                    }

                    // 감염 메시지 전송
                    for (Player player : playerList) {
                        player.sendMessage(serverTitle + " §4§l--]=====- §9§l" + event.getHuman().getName());
                    }

                    rewardDeath(human); // 데스 보상 함수 호출
                }
                return true;
            } else { // 플레이어가 이미 좀비일 경우
                return false;
            }
        } else { // 게임 진행 상태가 게임 중이 아닐 경우
            return false;
        }
    }

    // 감염 함수
    public boolean infected(Player human, Player zombie) {
        if (gameProgress == GameProgress.GAME) { // 게임 진행 상태가 게임 중일 경우
            if (!zombieList.contains(human)) { // 플레이어가 좀비가 아닐 경우
                // HumanInfectedByZombieEvent 이벤트 호출
                HumanInfectedByZombieEvent event = new HumanInfectedByZombieEvent(human, zombie);
                Bukkit.getServer().getPluginManager().callEvent(event);

                if (!event.isCancelled()) { // 이벤트가 취소되지 않았을 경우
                    if (humanList.contains(human)) { // 플레이어가 인간일 경우
                        if (livingHumanList.contains(human)) { // 플레이어가 생존한 인간일 경우
                            livingHumanList.remove(human); // 생존한 플레이어 목록에서 플레이어 제거
                        }
                        if (heroList.contains(human)) { // 플레이어가 영웅일 경우
                            heroList.remove(human); // 영웅 목록에서 플레이어 제거
                        }
                        humanList.remove(human); // 인간 목록에서 플레이어 제거
                        humanTeam.removeEntry(human.getName()); // 인간 팀에서 플레이어 제거
                    }
                    zombieList.add(human); // 좀비 목록에 플레이어 추가
                    zombieTeam.addEntry(human.getName()); // 좀비 팀에 플레이어 추가
                    convertZombie(human); // 좀비 변환 함추 호출
                    if (carrierList.contains(human)) { // 플레이어가 보균자일 경우
                        convertCarrier(human); // 보균자 변환 함수 호출
                        human.sendTitle("§c§l보균자", "§f모든 §9인간§f을 감염시키십시오!", 5, 100, 5);
                    } else { // 플레이어가 보균자가 아닐 경우
                        human.sendTitle("§c§l감염자", "§f모든 §9인간§f을 감염시키십시오!", 5, 100, 5);
                    }

                    // 감염 메시지 전송
                    for (Player player : playerList) {
                        player.sendMessage(serverTitle + " §c§l" + event.getZombie().getName() + " §4§l--]=====- §9§l" + event.getHuman().getName());
                    }

                    rewardKill(zombie); // 킬 보상 함수 호출
                    rewardDeath(human); // 데스 보상 함수 호출
                }
                return true;
            } else { // 플레이어가 이미 좀비일 경우
                return false;
            }
        } else { // 게임 진행 상태가 게임 중이 아닐 경우
            return false;
        }
    }

    // 사망 함수
    public boolean killed(Player zombie) {
        if (gameProgress == GameProgress.GAME) {
            if (zombieList.contains(zombie)) { // 플레이어가 좀비일 경우
                // ZombieKilledEvent 이벤트 호출
                ZombieKilledEvent event = new ZombieKilledEvent(zombie);
                Bukkit.getServer().getPluginManager().callEvent(event);

                if (!event.isCancelled()) { // 이벤트가 취소되지 않았을 경우

                    // 사망 메시지 전송
                    for (Player player : playerList) {
                        player.sendMessage(serverTitle + " §1§l┏√守━── §c§l" + event.getZombie().getName());
                    }

                    rewardDeath(zombie); // 데스 보상 함수 호출
                }
                return true;
            } else { // 플레이어가 좀비가 아닐 경우
                return false;
            }
        } else { // 게임 진행 상태가 게임 중이 아닐 경우
            return false;
        }
    }

    // 사망 함수
    public boolean killed(Player zombie, Player human) {
        if (gameProgress == GameProgress.GAME) {
            if (zombieList.contains(zombie)) { // 플레이어가 좀비일 경우
                // ZombieKilledByHumanEvent 이벤트 호출
                ZombieKilledByHumanEvent event = new ZombieKilledByHumanEvent(zombie, human);
                Bukkit.getServer().getPluginManager().callEvent(event);

                if (!event.isCancelled()) { // 이벤트가 취소되지 않았을 경우

                    // 사망 메시지 전송
                    for (Player player : playerList) {
                        player.sendMessage(serverTitle + " §9§l" + event.getHuman().getName() + " §1§l┏√守━── §c§l" + event.getZombie().getName());
                    }

                    rewardKill(human); // 킬 보상 함수 호출
                    rewardDeath(zombie); // 데스 보상 함수 호출
                }
                return true;
            } else { // 플레이어가 좀비가 아닐 경우
                return false;
            }
        } else { // 게임 진행 상태가 게임 중이 아닐 경우
            return false;
        }
    }

    // 치유 함수
    public boolean cured(Player zombie) {
        if (gameProgress == GameProgress.GAME) { // 게임 진행 상태가 게임 중일 경우
            if (!humanList.contains(zombie)) { // 플레이어가 인간이 아닐 경우
                // ZombieCuredEvent 이벤트 호출
                ZombieCuredEvent event = new ZombieCuredEvent(zombie);
                Bukkit.getServer().getPluginManager().callEvent(event);

                if (!event.isCancelled()) { // 이벤트가 취소되지 않았을 경우
                    if (zombieList.contains(zombie)) { // 플레이어가 좀비일 경우
                        if (hostList.contains(zombie)) { // 플레이어가 숙주일 경우
                            hostList.remove(zombie); // 숙주 목록에서 플레이어 제거
                        }
                        if (carrierList.contains(zombie)) { // 플레이어가 보균자일 경우
                            carrierList.remove(zombie); // 보균자 목록에서 플레이어 제거
                        }
                        zombieList.remove(zombie); // 좀비 목록에서 플레이어 제거
                        zombieTeam.removeEntry(zombie.getName()); // 좀비 팀에서 플레이어 제거
                    }
                    humanList.add(zombie); // 인간 목록에 플레이어 추가
                    humanTeam.addEntry(zombie.getName()); // 인간 팀에 플레이어 추가
                    convertHuman(zombie); // 인간 변환 함수 호출
                    zombie.sendTitle("§9§l생존자", "§c좀비§f를 피해 생존하십시오!", 5, 100, 5);

                    // 치유 메시지 전송
                    for (Player player : playerList) {
                        player.sendMessage(serverTitle + " §2§l|--|=====---- §c§l" + event.getZombie().getName());
                    }

                    rewardDeath(zombie); // 데스 보상 함수 호출
                }
                return true;
            } else { // 플레이어가 이미 인간일 경우
                return false;
            }
        } else { // 게임 진행 상태가 게임 중이 아닐 경우
            return false;
        }
    }

    // 치유 함수
    public boolean cured(Player zombie, Player human) {
        if (gameProgress == GameProgress.GAME) { // 게임 진행 상태가 게임 중일 경우
            if (!humanList.contains(zombie)) { // 플레이어가 인간이 아닐 경우
                // ZombieCuredByHumanEvent 이벤트 호출
                ZombieCuredByHumanEvent event = new ZombieCuredByHumanEvent(zombie, human);
                Bukkit.getServer().getPluginManager().callEvent(event);

                if (!event.isCancelled()) { // 이벤트가 취소되지 않았을 경우
                    if (zombieList.contains(zombie)) { // 플레이어가 좀비일 경우
                        if (hostList.contains(zombie)) { // 플레이어가 숙주일 경우
                            hostList.remove(zombie); // 숙주 목록에서 플레이어 제거
                        }
                        if (carrierList.contains(zombie)) { // 플레이어가 보균자일 경우
                            carrierList.remove(zombie); // 보균자 목록에서 플레이어 제거
                        }
                        zombieList.remove(zombie); // 좀비 목록에서 플레이어 제거
                        zombieTeam.removeEntry(zombie.getName()); // 좀비 팀에서 플레이어 제거
                    }
                    humanList.add(zombie); // 인간 목록에 플레이어 추가
                    humanTeam.addEntry(zombie.getName()); // 인간 팀에 플레이어 추가
                    convertHuman(zombie); // 인간 변환 함수 호출
                    zombie.sendTitle("§9§l생존자", "§c좀비§f를 피해 생존하십시오!", 5, 100, 5);

                    // 치유 메시지 전송
                    for (Player player : playerList) {
                        player.sendMessage(serverTitle + " §9§l" + event.getHuman().getName() + " §2§l|--|=====---- §c§l" + event.getZombie().getName());
                    }

                    rewardCure(human); // 치유 보상 함수 호출
                    rewardDeath(zombie); // 데스 보상 함수 호출
                }
                return true;
            } else { // 플레이어가 이미 인간일 경우
                return false;
            }
        } else { // 게임 진행 상태가 게임 중이 아닐 경우
            return false;
        }
    }

}