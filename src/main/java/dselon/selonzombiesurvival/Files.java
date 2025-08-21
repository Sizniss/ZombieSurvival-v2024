package dselon.selonzombiesurvival;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import org.bukkit.Bukkit;
import org.bukkit.Location;

import java.io.*;
import java.lang.reflect.Type;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

import static dselon.selonzombiesurvival.SelonZombieSurvival.files;

public class Files {

    public static JsonObject config;
    public static JsonObject data;


    public Files() {
        loadConfig(); // 콘피그 불러오기
        loadData(); // 데이터 불러오기
    }


    public static JsonArray fromArrayListLocationToJsonArray(ArrayList<Location> arrayList) {
        /*
        Gson gson = new Gson();
        JsonArray jsonArray = gson.toJsonTree(arrayList).getAsJsonArray();

        return jsonArray;
         */

        JsonArray jsonArray = new JsonArray();

        for(Location entry : arrayList) {
            jsonArray.add(toJsonObject(entry));
        }

        return jsonArray;
    }

    public static JsonArray fromArrayListJsonObjectToJsonArray(ArrayList<JsonObject> arrayList) {
        /*
        Gson gson = new Gson();
        JsonArray jsonArray = gson.toJsonTree(arrayList).getAsJsonArray();

        return jsonArray;
         */

        JsonArray jsonArray = new JsonArray();

        for(JsonObject entry : arrayList) {
            jsonArray.add(entry);
        }

        return jsonArray;
    }

    public static JsonObject toJsonObject(Location location) {
        JsonObject jsonObject = new JsonObject();

        jsonObject.addProperty("world",location.getWorld().getName());

        jsonObject.addProperty("x",location.getX());
        jsonObject.addProperty("y",location.getY());
        jsonObject.addProperty("z",location.getZ());

        jsonObject.addProperty("yaw",location.getYaw());
        jsonObject.addProperty("pitch",location.getPitch());

        return jsonObject;
    }

    public static ArrayList<JsonObject> toArrayListJsonObject(JsonArray jsonArray) {
        /*
        Gson gson = new Gson();
        Type listType = new TypeToken<ArrayList<JsonObject>>(){}.getType();
        ArrayList<JsonObject> arrayList = gson.fromJson(jsonArray, listType);

        return arrayList;
         */

        ArrayList<JsonObject> arrayList = new ArrayList<JsonObject>();

        for(JsonElement entry : jsonArray) {
            arrayList.add(entry.getAsJsonObject());
        }

        return arrayList;
    }

    public static ArrayList<Location> toArrayListLocation(JsonArray jsonArray) {
        /*
        Gson gson = new Gson();
        Type listType = new TypeToken<ArrayList<Location>>(){}.getType();
        ArrayList<Location> arrayList = gson.fromJson(jsonArray, listType);

        return arrayList;
         */

        ArrayList<Location> arrayList = new ArrayList<Location>();

        for(JsonElement entry : jsonArray) {
            arrayList.add(toLocation(entry.getAsJsonObject()));
        }

        return arrayList;
    }

    public static Location toLocation(JsonObject jsonObject) {
        Location location = new Location(Bukkit.getWorld(jsonObject.get("world").getAsString()), jsonObject.get("x").getAsDouble(), jsonObject.get("y").getAsDouble(), jsonObject.get("z").getAsDouble());
        location.setYaw(jsonObject.get("yaw").getAsFloat());
        location.setPitch(jsonObject.get("pitch").getAsFloat());

        return location;
    }


    // 서버 이름
    public static String getServerTitle() {
        return config.get("ServerTitle").getAsString();
    }
    public static void setServerTitle(String serverTitle) { config.addProperty("ServerTitle", serverTitle); }

    // 자동 시작 여부
    public static boolean getAutoStart() {
        return config.get("AutoStart").getAsBoolean();
    }
    public static void setAutoStart(boolean autoStart) { config.addProperty("AutoStart", autoStart); }

    // 대기 시간
    public static int getRestTime() {
        return config.get("RestTime").getAsInt();
    }
    public static void setRestTime(int restTime) { config.addProperty("RestTime", restTime); }

    // 준비 시간
    public static int getReadyTime() {
        return config.get("ReadyTime").getAsInt();
    }
    public static void setReadyTime(int readyTime) { config.addProperty("ReadyTime", readyTime); }

    // 게임 시간
    public static int getRoundTime() {
        return config.get("RoundTime").getAsInt();
    }
    public static void setRoundTime(int roundTime) { config.addProperty("RoundTime", roundTime); }

    // 라운드 수
    public static int getRoundCount() {
        return config.get("RoundCount").getAsInt();
    }
    public static void setRoundCount(int roundCount) { config.addProperty("RoundCount", roundCount); }

    // 숙주
    public static JsonObject getHost() {
        return config.get("Host").getAsJsonObject();
    }

    // 숙주 시작 인원 수
    public static int getHostStartCount() {
        return getHost().get("StartCount").getAsInt();
    }
    public static void setHostStartCount(int hostStartCount) {
        getHost().addProperty("StartCount", hostStartCount);
    }

    // 숙주 인원 간격 수
    public static int getHostIntervalCount() {
        return getHost().get("IntervalCount").getAsInt();
    }
    public static void setHostIntervalCount(int hostIntervalCount) { getHost().addProperty("IntervalCount", hostIntervalCount); }

    // 숙주 최대 인원 수
    public static int getHostMaxCount() {
        return getHost().get("MaxCount").getAsInt();
    }
    public static void setHostMaxCount(int hostMaxCount) { getHost().addProperty("MaxCount", hostMaxCount); }

    // 영웅
    public static JsonObject getHero() { return config.get("Hero").getAsJsonObject(); }

    // 영웅 시작 인원 수
    public static int getHeroStartCount() { return getHero().get("StartCount").getAsInt(); }
    public static void setHeroStartCount(int heroStartCount) { getHero().addProperty("StartCount", heroStartCount); }

    // 영웅 인원 간격 수
    public static int getHeroIntervalCount() { return getHero().get("IntervalCount").getAsInt(); }
    public static void setHeroIntervalCount(int heroIntervalCount) { getHero().addProperty("IntervalCount", heroIntervalCount); }

    // 영웅 최대 인원 수
    public static int getHeroMaxCount() { return getHero().get("MaxCount").getAsInt(); }
    public static void setHeroMaxCount(int heroMaxCount) { getHero().addProperty("MaxCount", heroMaxCount); }

    // 보균자
    public static JsonObject getCarrier() { return config.get("Carrier").getAsJsonObject(); }

    // 보균자 발현 최소 인원
    public static int getCarrierMinCount() { return getCarrier().get("MinCount").getAsInt(); }
    public static void setCarrierMinCount(int carrierMinCount) { getCarrier().addProperty("MinCount", carrierMinCount); }

    // 보균자 발현 횟수
    public static int getCarrierAppearanceCount() { return getCarrier().get("AppearanceCount").getAsInt(); }
    public static void setCarrierAppearanceCount(int carrierAppearanceCount) { getCarrier().addProperty("AppearanceCount", carrierAppearanceCount); }

    // 보균자 발현을 위한 인간과 좀비 비율
    public static float getCarrierRatio() { return getCarrier().get("Ratio").getAsFloat(); }
    public static void setCarrierRatio(float carrierRatio) { getCarrier().addProperty("Ratio", carrierRatio); }

    // 보급
    public static JsonObject getSupply() { return config.get("Supply").getAsJsonObject(); }

    // 보급 투하 횟수
    public static int getSupplyAppearanceCount() { return getSupply().get("AppearanceCount").getAsInt(); }
    public static void setSupplyAppearanceCount(int supplyAppearanceCount) { getSupply().addProperty("AppearanceCount", supplyAppearanceCount); }

    // 명령어
    public static JsonObject getCommand() { return config.get("Command").getAsJsonObject(); }

    // 기본 변환 명령어
    public static String getCommandConvertDefault() { return getCommand().get("ConvertDefault").getAsString(); }
    public static void setCommandConvertDefault(String commandConvertDefault) { getCommand().addProperty("ConvertDefault", commandConvertDefault); }

    // 인간 변환 명령어
    public static String getCommandConvertHuman() { return getCommand().get("ConvertHuman").getAsString(); }
    public static void setCommandConvertHuman(String commandConvertHuman) { getCommand().addProperty("ConvertHuman", commandConvertHuman); }

    // 좀비 변환 명령어
    public static String getCommandConvertZombie() { return getCommand().get("ConvertZombie").getAsString(); }
    public static void setCommandConvertZombie(String commandConvertZombie) { getCommand().addProperty("ConvertZombie", commandConvertZombie); }

    // 숙주 변환 명령어
    public static String getCommandConvertHost() { return getCommand().get("ConvertHost").getAsString(); }
    public static void setCommandConvertHost(String commandConvertHost) { getCommand().addProperty("ConvertHost", commandConvertHost); }

    // 영웅 변환 명령어
    public static String getCommandConvertHero() { return getCommand().get("ConvertHero").getAsString(); }
    public static void setCommandConvertHero(String commandConvertHero) { getCommand().addProperty("ConvertHero", commandConvertHero); }

    // 보균자 변환 명령어
    public static String getCommandConvertCarrier() { return getCommand().get("ConvertCarrier").getAsString(); }
    public static void setCommandConvertCarrier(String commandConvertCarrier) { getCommand().addProperty("ConvertCarrier", commandConvertCarrier); }

    // 보급 획득 명령어
    public static String getCommandAcquiredSupply() { return getCommand().get("AcquiredSupply").getAsString(); }
    public static void setCommandAcquiredSupply(String commandAcquiredSupply) { getCommand().addProperty("AcquiredSupply", commandAcquiredSupply); }

    // 승리 보상 명령어
    public static String getCommandRewardWin() { return getCommand().get("RewardWin").getAsString(); }
    public static void setCommandRewardWin(String commandRewardWin) { getCommand().addProperty("RewardWin", commandRewardWin); }

    // 패배 보상 명령어
    public static String getCommandRewardLose() { return getCommand().get("RewardLose").getAsString(); }
    public static void setCommandRewardLose(String commandRewardLose) { getCommand().addProperty("RewardLose", commandRewardLose); }

    // 킬 보상 명령어
    public static String getCommandRewardKill() { return getCommand().get("RewardKill").getAsString(); }
    public static void setCommandRewardKill(String commandRewardKill) { getCommand().addProperty("RewardKill", commandRewardKill); }

    // 치유 보상 명령어
    public static String getCommandRewardCure() { return getCommand().get("RewardCure").getAsString(); }
    public static void setCommandRewardCure(String commandRewardCure) { getCommand().addProperty("RewardCure", commandRewardCure); }

    // 데스 보상 명령어
    public static String getCommandRewardDeath() { return getCommand().get("RewardDeath").getAsString(); }
    public static void setCommandRewardDeath(String commandRewardDeath) { getCommand().addProperty("RewardDeath", commandRewardDeath); }

    // 탈주 패널티 명령어
    public static String getCommandEscapePenalty() { return getCommand().get("EscapePenalty").getAsString(); }
    public static void setCommandEscapePenalty(String commandEscapePenalty) { getCommand().addProperty("EscapePenalty", commandEscapePenalty); }


    // 로비 위치
    public static Location getLobbyLocation() { return toLocation(data.get("LobbyLocation").getAsJsonObject()); }
    public static void setLobbyLocation(Location lobbyLocation) {
        data.add("LobbyLocation", toJsonObject(lobbyLocation));

        files.saveData(); // 데이터 저장
    }

    // 맵 목록
    public static JsonObject getMapList() {
        return data.get("Map").getAsJsonObject();
    }
    public static void setMapList(JsonObject mapList) {
        data.add("Map", mapList);

        files.saveData(); // 데이터 저장
    }

    // 맵
    public static JsonObject getMap(String mapName) {
        // return getMapList().get(mapName).getAsJsonObject();
        return getMapList().get(mapName) == null ? null : getMapList().get(mapName).getAsJsonObject();
    }
    public static boolean addMap(String mapName) {
        if (getMap(mapName) == null) { // 맵이 없을 경우
            removeMap("Sample");

            JsonObject jsonObject = new JsonObject();

            ArrayList<Location> locationArrayList = new ArrayList<Location>();
            locationArrayList.add(new Location(Bukkit.getWorld("world"), 0, 0, 0));
            jsonObject.add("Location", fromArrayListLocationToJsonArray(locationArrayList));
            jsonObject.addProperty("RestTime", -1);
            jsonObject.addProperty("ReadyTime", -1);
            jsonObject.addProperty("RoundTime", -1);
            jsonObject.addProperty("RoundCount", -1);

            JsonObject host = new JsonObject();
            host.addProperty("StartCount", -1);
            host.addProperty("IntervalCount", -1);
            host.addProperty("MaxCount", -1);
            jsonObject.add("Host", host);

            JsonObject hero = new JsonObject();
            hero.addProperty("Enable", false);
            hero.addProperty("StartCount", -1);
            hero.addProperty("IntervalCount", -1);
            hero.addProperty("MaxCount", -1);
            jsonObject.add("Hero", hero);

            JsonObject carrier = new JsonObject();
            carrier.addProperty("Enable", false);
            carrier.addProperty("MinCount", -1);
            carrier.addProperty("AppearanceCount", -1);
            carrier.addProperty("Ratio", -1);
            jsonObject.add("Carrier", carrier);

            JsonObject supply = new JsonObject();
            supply.addProperty("Enable", false);
            supply.addProperty("AppearanceCount", -1);
            ArrayList<Location> supplyLocationArrayList = new ArrayList<Location>();
            supplyLocationArrayList.add(new Location(Bukkit.getWorld("world"), 0, 0, 0));
            supply.add("Location", fromArrayListLocationToJsonArray(supplyLocationArrayList));
            jsonObject.add("Supply", supply);

            JsonObject command = new JsonObject();
            command.addProperty("ConvertDefault", "default");
            command.addProperty("ConvertHuman", "default");
            command.addProperty("ConvertZombie", "default");
            command.addProperty("ConvertHost", "default");
            command.addProperty("ConvertHero", "default");
            command.addProperty("ConvertCarrier", "default");
            command.addProperty("AcquiredSupply", "default");
            command.addProperty("RewardWin", "default");
            command.addProperty("RewardLose", "default");
            command.addProperty("RewardKill", "default");
            command.addProperty("RewardCure", "default");
            command.addProperty("RewardDeath", "default");
            command.addProperty("EscapePenalty", "default");
            jsonObject.add("Command", command);

            JsonObject map = data.get("Map").getAsJsonObject();
            map.add(mapName, jsonObject);

            files.saveData(); // 데이터 저장

            return true;
        } else { // 맵이 이미 있을 경우
            return false;
        }
    }
    public static boolean addMap(String mapName, ArrayList<Location> locationArrayList) {
        if (getMap(mapName) == null) { // 맵이 없을 경우
            removeMap("Sample");

            JsonObject jsonObject = new JsonObject();

            JsonArray jsonArray = new JsonArray();
            Location location = locationArrayList.get(0);
            Location roundLocation = location;
            roundLocation.setX(Math.round(location.getX()*10)/10.0);
            roundLocation.setY(Math.round(location.getY()*10)/10.0);
            roundLocation.setZ(Math.round(location.getZ()*10)/10.0);
            roundLocation.setYaw((float)(Math.round(location.getYaw()*10)/10.0));
            roundLocation.setPitch((float)(Math.round(location.getPitch()*10)/10.0));
            jsonArray.add(toJsonObject(roundLocation));
            jsonObject.add("Location", jsonArray);
            jsonObject.addProperty("RestTime", -1);
            jsonObject.addProperty("ReadyTime", -1);
            jsonObject.addProperty("RoundTime", -1);
            jsonObject.addProperty("RoundCount", -1);

            JsonObject host = new JsonObject();
            host.addProperty("StartCount", -1);
            host.addProperty("IntervalCount", -1);
            host.addProperty("MaxCount", -1);
            jsonObject.add("Host", host);

            JsonObject hero = new JsonObject();
            hero.addProperty("Enable", false);
            hero.addProperty("StartCount", -1);
            hero.addProperty("IntervalCount", -1);
            hero.addProperty("MaxCount", -1);
            jsonObject.add("Hero", hero);

            JsonObject carrier = new JsonObject();
            carrier.addProperty("Enable", false);
            carrier.addProperty("MinCount", -1);
            carrier.addProperty("AppearanceCount", -1);
            carrier.addProperty("Ratio", -1);
            jsonObject.add("Carrier", carrier);

            JsonObject supply = new JsonObject();
            supply.addProperty("Enable", false);
            supply.addProperty("AppearanceCount", -1);
            ArrayList<Location> supplyLocationArrayList = new ArrayList<Location>();
            supplyLocationArrayList.add(new Location(Bukkit.getWorld("world"), 0, 0, 0));
            supply.add("Location", fromArrayListLocationToJsonArray(supplyLocationArrayList));
            jsonObject.add("Supply", supply);

            JsonObject command = new JsonObject();
            command.addProperty("ConvertDefault", "default");
            command.addProperty("ConvertHuman", "default");
            command.addProperty("ConvertZombie", "default");
            command.addProperty("ConvertHost", "default");
            command.addProperty("ConvertHero", "default");
            command.addProperty("ConvertCarrier", "default");
            command.addProperty("AcquiredSupply", "default");
            command.addProperty("RewardWin", "default");
            command.addProperty("RewardLose", "default");
            command.addProperty("RewardKill", "default");
            command.addProperty("RewardCure", "default");
            command.addProperty("RewardDeath", "default");
            command.addProperty("EscapePenalty", "default");
            jsonObject.add("Command", command);

            JsonObject map = data.get("Map").getAsJsonObject();
            map.add(mapName, jsonObject);

            files.saveData(); // 데이터 저장

            return true;
        } else { // 맵이 이미 있을 경우
            return false;
        }
    }
    public static boolean removeMap(String mapName) {
        if (getMap(mapName) != null) {
            getMapList().remove(mapName);

            files.saveData(); // 데이터 저장

            return true;
        } else {
            return false;
        }
    }

    // 맵 위치 목록
    public static ArrayList<Location> getMapLocation(String mapName) {
        return toArrayListLocation(getMap(mapName).get("Location").getAsJsonArray());
    }
    public static void setMapLocation(String mapName, ArrayList<Location> mapLocation) {
        getMap(mapName).add("Location", fromArrayListLocationToJsonArray(mapLocation));

        files.saveData(); // 데이터 저장
    }
    public static void addMapLocation(String mapName, Location location) {
        Location initLocation = new Location(Bukkit.getWorld("world"), 0, 0, 0, 0, 0);
        if (getMapLocation(mapName).contains(initLocation)) {
            JsonArray mapLocation = getMap(mapName).get("Location").getAsJsonArray();
            mapLocation.remove(toJsonObject(initLocation));
        }

        Location roundLocation = location;

        roundLocation.setX(Math.round(location.getX()*10)/10);
        roundLocation.setY(Math.round(location.getY()*10)/10);
        roundLocation.setZ(Math.round(location.getZ()*10)/10);
        roundLocation.setYaw(Math.round(location.getYaw()*10)/10);
        roundLocation.setPitch(Math.round(location.getPitch()*10)/10);

        JsonArray mapLocation = getMap(mapName).get("Location").getAsJsonArray();
        mapLocation.add(toJsonObject(roundLocation));

        files.saveData(); // 데이터 저장
    }
    public static void removeMapLocation(String mapName, int number) {
        JsonArray mapLocation = getMap(mapName).get("Location").getAsJsonArray();
        mapLocation.remove(number);

        files.saveData(); // 데이터 저장
    }

    // 맵의 대기 시간
    public static int getMapRestTime(String mapName) {
        return getMap(mapName).get("RestTime").getAsInt();
    }
    public static void setMapRestTime(String mapName, int restTime) {
        getMap(mapName).addProperty("RestTime", restTime);

        files.saveData(); // 데이터 저장
    }

    // 맵의 준비 시간
    public static int getMapReadyTime(String mapName) {
        return getMap(mapName).get("ReadyTime").getAsInt();
    }
    public static void setMapReadyTime(String mapName, int readyTime) {
        getMap(mapName).addProperty("ReadyTime", readyTime);

        files.saveData(); // 데이터 저장
    }

    // 맵의 게임 시간
    public static int getMapRoundTime(String mapName) {
        return getMap(mapName).get("RoundTime").getAsInt();
    }
    public static void setMapRoundTime(String mapName, int roundTime) {
        getMap(mapName).addProperty("RoundTime", roundTime);

        files.saveData(); // 데이터 저장
    }

    // 맵의 라운드 수
    public static int getMapRoundCount(String mapName) {
        return getMap(mapName).get("RoundCount").getAsInt();
    }
    public static void setMapRoundCount(String mapName, int roundCount) {
        getMap(mapName).addProperty("RoundCount", roundCount);

        files.saveData(); // 데이터 저장
    }

    // 맵의 숙주
    public static JsonObject getMapHost(String mapName) {
        return getMap(mapName).get("Host").getAsJsonObject();
    }

    // 맵의 숙주 시작 인원 수
    public static int getMapHostStartCount(String mapName) {
        return getMapHost(mapName).get("StartCount").getAsInt();
    }
    public static void setMapHostStartCount(String mapName, int hostStartCount) {
        getMapHost(mapName).addProperty("StartCount", hostStartCount);

        files.saveData(); // 데이터 저장
    }

    // 맵의 숙주 인원 간격 수
    public static int getMapHostIntervalCount(String mapName) {
        return getMapHost(mapName).get("IntervalCount").getAsInt();
    }
    public static void setMapHostIntervalCount(String mapName, int hostIntervalCount) {
        getMapHost(mapName).addProperty("IntervalCount", hostIntervalCount);

        files.saveData(); // 데이터 저장
    }

    // 맵의 숙주 최대 인원 수
    public static int getMapHostMaxCount(String mapName) {
        return getMapHost(mapName).get("MaxCount").getAsInt();
    }
    public static void setMapHostMaxCount(String mapName, int hostMaxCount) {
        getMapHost(mapName).addProperty("MaxCount", hostMaxCount);

        files.saveData(); // 데이터 저장
    }

    // 맵의 영웅
    public static JsonObject getMapHero(String mapName) {
        return getMap(mapName).get("Hero").getAsJsonObject();
    }

    // 맵의 영웅 사용 여부
    public static boolean getMapHeroEnable(String mapName) {
        return getMapHero(mapName).get("Enable").getAsBoolean();
    }
    public static void setMapHeroEnable(String mapName, boolean heroEnable) {
        getMapHero(mapName).addProperty("Enable", heroEnable);

        files.saveData(); // 데이터 저장
    }

    // 맵의 영웅 시작 인원 수
    public static int getMapHeroStartCount(String mapName) {
        return getMapHero(mapName).get("StartCount").getAsInt();
    }
    public static void setMapHeroStartCount(String mapName, int heroStartCount) {
        getMapHero(mapName).addProperty("StartCount", heroStartCount);

        files.saveData(); // 데이터 저장
    }

    // 맵의 영웅 인원 간격 수
    public static int getMapHeroIntervalCount(String mapName) {
        return getMapHero(mapName).get("IntervalCount").getAsInt();
    }
    public static void setMapHeroIntervalCount(String mapName, int heroIntervalCount) {
        getMapHero(mapName).addProperty("IntervalCount", heroIntervalCount);

        files.saveData(); // 데이터 저장
    }

    // 맵의 영웅 최대 인원 수
    public static int getMapHeroMaxCount(String mapName) {
        return getMapHero(mapName).get("MaxCount").getAsInt();
    }
    public static void setMapHeroMaxCount(String mapName, int heroMaxCount) {
        getMapHero(mapName).addProperty("MaxCount", heroMaxCount);

        files.saveData(); // 데이터 저장
    }

    // 맵의 보균자
    public static JsonObject getMapCarrier(String mapName) {
        return getMap(mapName).get("Carrier").getAsJsonObject();
    }

    // 맵의 보균자 사용 여부
    public static boolean getMapCarrierEnable(String mapName) {
        return getMapCarrier(mapName).get("Enable").getAsBoolean();
    }
    public static void setMapCarrierEnable(String mapName, boolean carrierEnable) {
        getMapCarrier(mapName).addProperty("Enable", carrierEnable);

        files.saveData(); // 데이터 저장
    }

    // 맵의 보균자 발현 최소 인원
    public static int getMapCarrierMinCount(String mapName) {
        return getMapCarrier(mapName).get("MinCount").getAsInt();
    }
    public static void setMapCarrierMinCount(String mapName, int carrierMinCount) {
        getMapCarrier(mapName).addProperty("MinCount", carrierMinCount);

        files.saveData(); // 데이터 저장
    }

    // 맵의 보균자 발현 횟수
    public static int getMapCarrierAppearanceCount(String mapName) {
        return getMapCarrier(mapName).get("AppearanceCount").getAsInt();
    }
    public static void setMapCarrierAppearanceCount(String mapName, int carrierAppearanceCount) {
        getMapCarrier(mapName).addProperty("AppearanceCount", carrierAppearanceCount);

        files.saveData(); // 데이터 저장
    }

    // 맵의 보균자 발현을 위한 인간과 좀비 비율
    public static float getMapCarrierRatio(String mapName) {
        return getMapCarrier(mapName).get("Ratio").getAsFloat();
    }
    public static void setMapCarrierRatio(String mapName, float carrierRatio) {
        getMapCarrier(mapName).addProperty("Ratio", carrierRatio);

        files.saveData(); // 데이터 저장
    }

    // 맵의 보급
    public static JsonObject getMapSupply(String mapName) {
        return getMap(mapName).get("Supply").getAsJsonObject();
    }

    // 맵의 보급 사용
    public static boolean getMapSupplyEnable(String mapName) { return getMapSupply(mapName).get("Enable").getAsBoolean(); }
    public static void setMapSupplyEnable(String mapName, boolean supplyEnable) {
        getMapSupply(mapName).addProperty("Enable", supplyEnable);

        files.saveData(); // 데이터 저장
    }

    // 맵의 보급 투하 횟수
    public static int getMapSupplyAppearanceCount(String mapName) {
        return getMapSupply(mapName).get("AppearanceCount").getAsInt();
    }
    public static void setMapSupplyAppearanceCount(String mapName, int supplyAppearanceCount) {
        getMapSupply(mapName).addProperty("AppearanceCount", supplyAppearanceCount);

        files.saveData(); // 데이터 저장
    }

    // 맵의 보급 위치
    public static ArrayList<Location> getMapSupplyLocation(String mapName) {
        return toArrayListLocation(getMapSupply(mapName).get("Location").getAsJsonArray());
    }
    public static void setMapSupplyLocation(String mapName, ArrayList<Location> supplyLocation) {
        getMapSupply(mapName).add("Location", fromArrayListLocationToJsonArray(supplyLocation));

        files.saveData(); // 데이터 저장
    }
    public static void addMapSupplyLocation(String mapName, Location location) {
        Location initLocation = new Location(Bukkit.getWorld("world"), 0, 0, 0, 0, 0);
        if (getMapSupplyLocation(mapName).contains(initLocation)) {
            JsonArray mapLocation = getMapSupply(mapName).get("Location").getAsJsonArray();
            mapLocation.remove(toJsonObject(initLocation));
        }

        Location roundLocation = location;

        roundLocation.setX(Math.floor(location.getX()));
        roundLocation.setY(Math.floor(location.getY()));
        roundLocation.setZ(Math.floor(location.getZ()));
        roundLocation.setYaw(0);
        roundLocation.setPitch(0);

        JsonArray supplyLocation = getMapSupply(mapName).get("Location").getAsJsonArray();
        supplyLocation.add(toJsonObject(roundLocation));

        files.saveData(); // 데이터 저장
    }
    public static void removeMapSupplyLocation(String mapName, int number) {
        JsonArray supplyLocation = getMapSupply(mapName).get("Location").getAsJsonArray();
        supplyLocation.remove(number);

        files.saveData(); // 데이터 저장
    }

    // 맵의 명령어
    public static JsonObject getMapCommand(String mapName) {
        return getMap(mapName).get("Command").getAsJsonObject();
    }

    // 맵의 기본 변환 명령어
    public static String getMapCommandConvertDefault(String mapName) {
        return getMapCommand(mapName).get("ConvertDefault").getAsString();
    }
    public static void setMapCommandConvertDefault(String mapName, String commandConvertDefault) {
        getMapCommand(mapName).addProperty("ConvertDefault", commandConvertDefault);

        files.saveData(); // 데이터 저장
    }

    // 맵의 인간 변환 명령어
    public static String getMapCommandConvertHuman(String mapName) {
        return getMapCommand(mapName).get("ConvertHuman").getAsString();
    }
    public static void setMapCommandConvertHuman(String mapName, String commandConvertHuman) {
        getMapCommand(mapName).addProperty("ConvertHuman", commandConvertHuman);

        files.saveData(); // 데이터 저장
    }

    // 맵의 좀비 변환 명령어
    public static String getMapCommandConvertZombie(String mapName) {
        return getMapCommand(mapName).get("ConvertZombie").getAsString();
    }
    public static void setMapCommandConvertZombie(String mapName, String commandConvertZombie) {
        getMapCommand(mapName).addProperty("ConvertZombie", commandConvertZombie);

        files.saveData(); // 데이터 저장
    }

    // 맵의 숙주 변환 명령어
    public static String getMapCommandConvertHost(String mapName) {
        return getMapCommand(mapName).get("ConvertHost").getAsString();
    }
    public static void setMapCommandConvertHost(String mapName, String commandConvertHost) {
        getMapCommand(mapName).addProperty("ConvertHost", commandConvertHost);

        files.saveData(); // 데이터 저장
    }

    // 맵의 영웅 변환 명령어
    public static String getMapCommandConvertHero(String mapName) {
        return getMapCommand(mapName).get("ConvertHero").getAsString();
    }
    public static void setMapCommandConvertHero(String mapName, String commandConvertHero) {
        getMapCommand(mapName).addProperty("ConvertHero", commandConvertHero);

        files.saveData(); // 데이터 저장
    }

    // 맵의 보균자 변환 명령어
    public static String getMapCommandConvertCarrier(String mapName) {
        return getMapCommand(mapName).get("ConvertCarrier").getAsString();
    }
    public static void setMapCommandConvertCarrier(String mapName, String commandConvertCarrier) {
        getMapCommand(mapName).addProperty("ConvertCarrier", commandConvertCarrier);

        files.saveData(); // 데이터 저장
    }

    // 맵의 보급 획득 명령어
    public static String getMapCommandAcquiredSupply(String mapName) {
        return getMapCommand(mapName).get("AcquiredSupply").getAsString();
    }
    public static void setMapCommandAcquiredSupply(String mapName, String commandAcquiredSupply) {
        getMapCommand(mapName).addProperty("AcquiredSupply", commandAcquiredSupply);

        files.saveData(); // 데이터 저장
    }

    // 맵의 승리 보상 명령어
    public static String getMapCommandRewardWin(String mapName) {
        return getMapCommand(mapName).get("RewardWin").getAsString();
    }
    public static void setMapCommandRewardWin(String mapName, String commandRewardWin) {
        getMapCommand(mapName).addProperty("RewardWin", commandRewardWin);

        files.saveData(); // 데이터 저장
    }

    // 맵의 패배 보상 명령어
    public static String getMapCommandRewardLose(String mapName) {
        return getMapCommand(mapName).get("RewardLose").getAsString();
    }
    public static void setMapCommandRewardLose(String mapName, String commandRewardLose) {
        getMapCommand(mapName).addProperty("RewardLose", commandRewardLose);

        files.saveData(); // 데이터 저장
    }

    // 맵의 킬 보상 명령어
    public static String getMapCommandRewardKill(String mapName) {
        return getMapCommand(mapName).get("RewardKill").getAsString();
    }
    public static void setMapCommandRewardKill(String mapName, String commandRewardKill) {
        getMapCommand(mapName).addProperty("RewardKill", commandRewardKill);

        files.saveData(); // 데이터 저장
    }

    // 맵의 치유 보상 명령어
    public static String getMapCommandRewardCure(String mapName) {
        return getMapCommand(mapName).get("RewardCure").getAsString();
    }
    public static void setMapCommandRewardCure(String mapName, String commandRewardCure) {
        getMapCommand(mapName).addProperty("RewardCure", commandRewardCure);

        files.saveData(); // 데이터 저장
    }

    // 맵의 데스 보상 명령어
    public static String getMapCommandRewardDeath(String mapName) {
        return getMapCommand(mapName).get("RewardDeath").getAsString();
    }
    public static void setMapCommandRewardDeath(String mapName, String commandRewardDeath) {
        getMapCommand(mapName).addProperty("RewardDeath", commandRewardDeath);

        files.saveData(); // 데이터 저장
    }

    // 맵의 탈주 패널티 명령어
    public static String getMapCommandEscapePenalty(String mapName) {
        return getMapCommand(mapName).get("EscapePenalty").getAsString();
    }
    public static void setMapCommandEscapePenalty(String mapName, String commandEscapePenalty) {
        getMapCommand(mapName).addProperty("EscapePenalty", commandEscapePenalty);

        files.saveData(); // 데이터 저장
    }

    
    // 콘피그 불러오기 함수
    public void loadConfig() {
        File file = new File(SelonZombieSurvival.plugin.getDataFolder(), "config.json");
        if (!file.exists()) {
            SelonZombieSurvival.plugin.saveResource("config.json", false);
        }
        try {
            InputStream inputStream = new FileInputStream(file);
            Reader reader = new InputStreamReader(inputStream, Charset.forName("UTF-8"));

            JsonParser parser = new JsonParser();
            config = parser.parse(reader).getAsJsonObject();
        } catch (FileNotFoundException ex) {
            ex.printStackTrace();
        }
    }
    
    // 콘피그 저장 함수
    public void saveConfig() {
        File file = new File(SelonZombieSurvival.plugin.getDataFolder(), "config.json");
        if (!file.exists()) {
            SelonZombieSurvival.plugin.saveResource("config.json", false);
        }
        try {
            Writer writer = new OutputStreamWriter(new FileOutputStream(file), StandardCharsets.UTF_8);

            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            gson.toJson(config, writer);
            writer.append(System.lineSeparator());
            writer.flush();
            writer.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
    
    // 데이터 불러오기 함수
    public void loadData() {
        File file = new File(SelonZombieSurvival.plugin.getDataFolder(), "data.json");
        if (!file.exists()) {
            SelonZombieSurvival.plugin.saveResource("data.json", false);
        }
        try {
            InputStream inputStream = new FileInputStream(file);
            Reader reader = new InputStreamReader(inputStream, Charset.forName("UTF-8"));

            JsonParser parser = new JsonParser();
            data = parser.parse(reader).getAsJsonObject();
        } catch (FileNotFoundException ex) {
            ex.printStackTrace();
        }
    }
    
    // 데이터 저장 함수
    public void saveData() {
        File file = new File(SelonZombieSurvival.plugin.getDataFolder(), "data.json");
        if (!file.exists()) {
            SelonZombieSurvival.plugin.saveResource("data.json", false);
        }
        try {
            Writer writer = new OutputStreamWriter(new FileOutputStream(file), StandardCharsets.UTF_8);

            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            gson.toJson(data, writer);
            writer.append(System.lineSeparator());
            writer.flush();
            writer.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}
