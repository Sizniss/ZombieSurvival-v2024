package dselon.selonzombiesurvival;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import dselon.selonzombiesurvival.customevents.PlayerEscapeEvent;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Map;

public class Commands implements CommandExecutor {

    private String pluginName = Files.getServerTitle(); // "§7§l[ §4§lSelonZS §7§l]"
    private String serverTitle = Files.getServerTitle();

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (sender.hasPermission("selonzs.op")) { // 오피 권한일 경우

            if (args.length > 0) {

                if (args[0].equalsIgnoreCase("Load")) {
                    Files files = SelonZombieSurvival.files;
                    files.loadConfig();
                    files.loadData();
                    sender.sendMessage(Files.getServerTitle() + " §f§l플러그인을 불러왔습니다!");

                } else if (args[0].equalsIgnoreCase("Save")) {
                    Files files = SelonZombieSurvival.files;
                    files.saveConfig();
                    files.saveData();
                    sender.sendMessage(Files.getServerTitle() + " §f§l플러그인을 저장했습니다!");

                } else if (args[0].equalsIgnoreCase("Reload")) {
                    SelonZombieSurvival plugin = SelonZombieSurvival.plugin;
                    Bukkit.getPluginManager().disablePlugin(plugin);
                    Bukkit.getPluginManager().enablePlugin(plugin);
                    sender.sendMessage(Files.getServerTitle() + " §f§l플러그인을 리로드하였습니다!");

                } else if (args[0].equalsIgnoreCase("Lobby")) {
                    if (args.length > 1) {
                        if (args[1].equalsIgnoreCase("Set")) {
                            Files.setLobbyLocation(Bukkit.getPlayer(sender.getName()).getLocation());
                            sender.sendMessage(serverTitle + " §f§l플레이어의 위치를 로비 위치로 설정하였습니다!");
                        }
                    } else {
                        sender.sendMessage("");
                        sender.sendMessage(" " + pluginName);
                        sender.sendMessage(" §f§l/SelonZS Lobby Set§7§l: §f플레이어의 위치를 로비 위치로 설정합니다.");
                    }

                } else if (args[0].equalsIgnoreCase("Map")) {
                    if (args.length > 1) {
                        if (args[1].equalsIgnoreCase("List")) {
                            sender.sendMessage("");
                            sender.sendMessage(" " + pluginName);
                            sender.sendMessage(" §f§l맵 목록:");
                            if (Files.getMapList().size() > 0) { // 등록된 맵이 0개 초과일 경우
                                for (Map.Entry<String, JsonElement> entry : Files.getMapList().entrySet()) {
                                    String key = entry.getKey();
                                    JsonElement value = entry.getValue();

                                    sender.sendMessage(" §f- §7" + key);
                                }
                            } else { // 등록된 맵이 0개일 경우
                                sender.sendMessage(" §f- []");
                            }
                        } else if (args[1].equalsIgnoreCase("Add")) {
                            if (args.length > 2) {
                                String mapName = args[2];
                                mapName = mapName.replace("_", " ");

                                ArrayList<Location> locationArrayList = new ArrayList<Location>();
                                locationArrayList.add(Bukkit.getPlayer(sender.getName()).getLocation());

                                if (Files.addMap(mapName, locationArrayList)) {
                                    sender.sendMessage(serverTitle + " §f§l'§e§l" + mapName + "§f§l' 맵을 추가하였습니다.");
                                } else {
                                    sender.sendMessage(serverTitle + " §f§l이미 존재하는 맵입니다!");
                                }
                            } else {
                                sender.sendMessage("");
                                sender.sendMessage(" " + pluginName);
                                sender.sendMessage(" §f§l/SelonZS Map Add §e§l<Map>§7§l: §f맵을 추가합니다.");
                            }
                        } else if (args[1].equalsIgnoreCase("Remove")) {
                            if (args.length > 2) {
                                String mapName = args[2];
                                mapName = mapName.replace("_", " ");

                                if (Files.removeMap(mapName)) {
                                    sender.sendMessage(serverTitle + " §f§l'§e§l" + mapName + "§f§l' 맵을 제거하였습니다.");
                                } else {
                                    sender.sendMessage(serverTitle + " §f§l존재하지 않는 맵입니다!");
                                }
                            } else {
                                sender.sendMessage("");
                                sender.sendMessage(" " + pluginName);
                                sender.sendMessage(" §f§l/SelonZS Map Remove §e§l<Map>§7§l: §f맵을 제거합니다.");
                            }
                        } else if (args[1].equalsIgnoreCase("Select")) {
                            if (args.length > 2) {
                                Manager manager = SelonZombieSurvival.manager;
                                JsonObject mapList = manager.getMapList();
                                String mapName = args[2];
                                mapName = mapName.replace("_", " ");

                                boolean isEmpty = true;
                                for (Map.Entry<String, JsonElement> entry : mapList.entrySet()) {
                                    String key = entry.getKey();

                                    if (key.equals(mapName)) {
                                        isEmpty = false;
                                        break;
                                    }
                                }
                                if (!isEmpty) {
                                    manager.setSelectedMapName(mapName);
                                    
                                    sender.sendMessage(serverTitle + " §f§l'§e§l" + mapName + "§f§l' 맵을 선택하였습니다.");
                                } else {
                                    sender.sendMessage(serverTitle + " §f§l존재하지 않는 맵입니다!");
                                }
                            } else {
                                sender.sendMessage("");
                                sender.sendMessage(" " + pluginName);
                                sender.sendMessage(" §f§l/SelonZS Map Select §e§l<Map>§7§l: §f맵을 선택합니다.");
                            }
                        }
                    } else {
                        sender.sendMessage("");
                        sender.sendMessage(" " + pluginName);
                        sender.sendMessage(" §f§l/SelonZS Map List§7§l: §f맵 목록을 확인합니다.");
                        sender.sendMessage(" §f§l/SelonZS Map Add §e§l<Map>§7§l: §f맵을 추가합니다.");
                        sender.sendMessage(" §f§l/SelonZS Map Remove §e§l<Map>§7§l: §f맵을 제거합니다.");
                        sender.sendMessage(" §f§l/SelonZS Map Select §e§l<Map>§7§l: §f맵을 선택합니다.");
                    }

                } else if (args[0].equalsIgnoreCase("MapOption")) {
                    if (args.length > 1) {
                        String mapName = args[1];
                        mapName = mapName.replace("_", " ");

                        if (Files.getMap(mapName) != null) {
                            if (args.length > 2) {
                                if (args[2].equalsIgnoreCase("Location")) {
                                    if (args.length > 3) {
                                        if (args[3].equalsIgnoreCase("List")) {
                                            sender.sendMessage("");
                                            sender.sendMessage(" " + pluginName);
                                            sender.sendMessage(" §e§l" + mapName);
                                            sender.sendMessage(" §f§l맵 위치 목록:");
                                            if (Files.getMapLocation(mapName).size() > 0) { // 맵 위치 개수가 0개 초과일 경우
                                                int number = 0;
                                                for (Location entry : Files.getMapLocation(mapName)) {
                                                    sender.sendMessage(" §f- §7" + number + ": " + entry.toString());
                                                    number++;
                                                }
                                            } else { // 맵 위치 개수가 0개일 경우
                                                sender.sendMessage(" §f- []");
                                            }
                                        } else if (args[3].equalsIgnoreCase("Add")) {
                                            Files.addMapLocation(mapName, Bukkit.getPlayer(sender.getName()).getLocation());
                                            sender.sendMessage(serverTitle + " §f§l플레이어의 위치를 '§e§l" + mapName + "§f§l' 맵 위치로 추가하였습니다.");
                                        } else if (args[3].equalsIgnoreCase("Remove")) {
                                            if (args.length > 4) {
                                                int number = Integer.parseInt(args[4]);

                                                Files.removeMapLocation(mapName, number);
                                                sender.sendMessage(serverTitle + " '§e§l" + mapName + "§f§l' 맵 위치를 제거하였습니다.");
                                            } else {
                                                sender.sendMessage("");
                                                sender.sendMessage(" " + pluginName);
                                                sender.sendMessage(" §f§l/SelonZS MapOption §e§l<Map> §f§lLocation Remove §e§l<Number>§7§l: §f맵 위치를 제거합니다.");
                                            }
                                        }
                                    } else {
                                        sender.sendMessage("");
                                        sender.sendMessage(" " + pluginName);
                                        sender.sendMessage(" §f§l/SelonZS MapOption §e§l<Map> §f§lLocation List§7§l: §f맵 위치를 확인합니다.");
                                        sender.sendMessage(" §f§l/SelonZS MapOption §e§l<Map> §f§lLocation Add§7§l: §f맵 위치를 추가합니다.");
                                        sender.sendMessage(" §f§l/SelonZS MapOption §e§l<Map> §f§lLocation Remove §e§l<Number>§7§l: §f맵 위치를 제거합니다.");
                                    }
                                } else if (args[2].equalsIgnoreCase("Supply")) {
                                    if (args.length > 3) {
                                        if (args[3].equalsIgnoreCase("Location")) {
                                            if (args.length > 4) {
                                                if (args[4].equalsIgnoreCase("List")) {
                                                    sender.sendMessage("");
                                                    sender.sendMessage(" " + pluginName);
                                                    sender.sendMessage(" §e§l" + mapName);
                                                    sender.sendMessage(" §f§l맵의 보급 위치 목록:");
                                                    if (Files.getMapSupplyLocation(mapName).size() > 0) { // 맵의 보급 위치 개수가 0개 초과일 경우
                                                        int number = 0;
                                                        for (Location entry : Files.getMapSupplyLocation(mapName)) {
                                                            sender.sendMessage(" §f- §7" + number + ": " + entry.toString());
                                                            number++;
                                                        }
                                                    } else { // 맵의 보급 위치 개수가 0개일 경우
                                                        sender.sendMessage(" §f- []");
                                                    }
                                                } else if (args[4].equalsIgnoreCase("Add")) {
                                                    Files.addMapSupplyLocation(mapName, Bukkit.getPlayer(sender.getName()).getLocation());
                                                    sender.sendMessage(serverTitle + " §f§l플레이어의 위치를 '§e§l" + mapName + "§f§l' 맵의 보급 위치 목록에 추가하였습니다.");
                                                } else if (args[4].equalsIgnoreCase("Remove")) {
                                                    if (args.length > 5) {
                                                        int number = Integer.parseInt(args[5]);

                                                        Files.removeMapSupplyLocation(mapName, number);
                                                        sender.sendMessage(serverTitle + " '§e§l" + mapName + "§f§l' 맵의 보급 위치를 제거하였습니다.");
                                                    } else {
                                                        sender.sendMessage("");
                                                        sender.sendMessage(" " + pluginName);
                                                        sender.sendMessage(" §f§l/SelonZS MapOption §e§l<Map> §f§lSupply Location Remove §e§l<Number>§7§l: §f...");
                                                    }
                                                }
                                            } else {
                                                sender.sendMessage("");
                                                sender.sendMessage(" " + pluginName);
                                                sender.sendMessage(" §f§l/SelonZS MapOption §e§l<Map> §f§lSupply Location List§7§l: §f보급 위치를 확인합니다.");
                                                sender.sendMessage(" §f§l/SelonZS MapOption §e§l<Map> §f§lSupply Location Add§7§l: §f보급 위치를 추가합니다.");
                                                sender.sendMessage(" §f§l/SelonZS MapOption §e§l<Map> §f§lSupply Location Remove §e§l<Number>§7§l: §f...");
                                            }
                                        }
                                    } else {
                                        sender.sendMessage("");
                                        sender.sendMessage(" " + pluginName);
                                        sender.sendMessage(" §f§l/SelonZS MapOption §e§l<Map> §f§lSupply Location List§7§l: §f보급 위치를 확인합니다.");
                                        sender.sendMessage(" §f§l/SelonZS MapOption §e§l<Map> §f§lSupply Location Add§7§l: §f보급 위치를 추가합니다.");
                                        sender.sendMessage(" §f§l/SelonZS MapOption §e§l<Map> §f§lSupply Location Remove §e§l<Number>§7§l: §f...");
                                    }
                                }
                            } else {
                                sender.sendMessage("");
                                sender.sendMessage(" " + pluginName);
                                sender.sendMessage(" §f§l/SelonZS MapOption §e§l<Map> §f§lLocation List§7§l: §f맵 위치를 확인합니다.");
                                sender.sendMessage(" §f§l/SelonZS MapOption §e§l<Map> §f§lLocation Add§7§l: §f맵 위치를 추가합니다.");
                                sender.sendMessage(" §f§l/SelonZS MapOption §e§l<Map> §f§lLocation Remove §e§l<Number>§7§l: §f맵 위치를 제거합니다.");
                                sender.sendMessage(" §f§l/SelonZS MapOption §e§l<Map> §f§lSupply Location List§7§l: §f보급 위치를 확인합니다.");
                                sender.sendMessage(" §f§l/SelonZS MapOption §e§l<Map> §f§lSupply Location Add§7§l: §f보급 위치를 추가합니다.");
                                sender.sendMessage(" §f§l/SelonZS MapOption §e§l<Map> §f§lSupply Location Remove §e§l<Number>§7§l: §f...");
                            }
                        } else {
                            sender.sendMessage(serverTitle + " §f§l존재하지 않는 맵입니다!");
                        }
                    } else {
                        sender.sendMessage("");
                        sender.sendMessage(" " + pluginName);
                        sender.sendMessage(" §f§l/SelonZS MapOption §e§l<Map> §f§lLocation List§7§l: §f맵 위치를 확인합니다.");
                        sender.sendMessage(" §f§l/SelonZS MapOption §e§l<Map> §f§lLocation Add§7§l: §f맵 위치를 추가합니다.");
                        sender.sendMessage(" §f§l/SelonZS MapOption §e§l<Map> §f§lLocation Remove §e§l<Number>§7§l: §f맵 위치를 제거합니다.");
                        sender.sendMessage(" §f§l/SelonZS MapOption §e§l<Map> §f§lSupply Location List§7§l: §f보급 위치를 확인합니다.");
                        sender.sendMessage(" §f§l/SelonZS MapOption §e§l<Map> §f§lSupply Location Add§7§l: §f보급 위치를 추가합니다.");
                        sender.sendMessage(" §f§l/SelonZS MapOption §e§l<Map> §f§lSupply Location Remove §e§l<Number>§7§l: §f...");
                    }

                } else if (args[0].equalsIgnoreCase("Game")) {
                    if (args.length > 1) {
                        if (args[1].equalsIgnoreCase("Start")) {
                            if (SelonZombieSurvival.manager.gameStart()) {
                                sender.sendMessage(serverTitle + " §f§l좀비 서바이벌 게임을 시작하였습니다!");
                                if (!sender.getName().equals("CONSOLE")) {
                                    for (Player player : SelonZombieSurvival.manager.getPlayerList()) {
                                        if (player == (Player) sender) {
                                            continue;
                                        }
                                        player.sendMessage(serverTitle + " §f§l관리자(§6§l" + sender.getName() + "§f§l)에 의하여 게임이 시작되었습니다.");
                                    }
                                } else {
                                    for (Player player : SelonZombieSurvival.manager.getPlayerList()) {
                                        player.sendMessage(serverTitle + " §f§l서버에 의하여 게임이 시작되었습니다.");
                                    }
                                }
                            } else {
                                sender.sendMessage(serverTitle + " §f§l이미 좀비 서바이벌 게임이 진행 중입니다!");
                            }
                        } else if (args[1].equalsIgnoreCase("Stop")) {
                            if (SelonZombieSurvival.manager.gameStop()) {
                                sender.sendMessage(serverTitle + " §f§l좀비 서바이벌 게임을 중지하였습니다!");
                                if (!sender.getName().equals("CONSOLE")) {
                                    for (Player player : SelonZombieSurvival.manager.getPlayerList()) {
                                        if (player == (Player) sender) {
                                            continue;
                                        }
                                        player.sendMessage(serverTitle + " §f§l관리자(§6§l" + sender.getName() + "§f§l)에 의하여 게임이 중지되었습니다.");
                                    }
                                } else {
                                    for (Player player : SelonZombieSurvival.manager.getPlayerList()) {
                                        player.sendMessage(serverTitle + " §f§l서버에 의하여 게임이 중지되었습니다.");
                                    }
                                }
                            } else {
                                sender.sendMessage(serverTitle + " §f§l이미 좀비 서바이벌 게임을 진행하고 있지 않습니다!");
                            }
                        }
                    } else {
                        sender.sendMessage("");
                        sender.sendMessage(" " + pluginName);
                        sender.sendMessage(" §f§l/SelonZS Game Start§7§l: §f좀비 서바이벌 게임을 시작합니다.");
                        sender.sendMessage(" §f§l/SelonZS Game Stop§7§l: §f좀비 서바이벌 게임을 중지합니다.");
                    }

                } else if (args[0].equalsIgnoreCase("Player")) {
                    if (args.length > 1) {
                        if (args[1].equalsIgnoreCase("List")) {
                            sender.sendMessage("");
                            sender.sendMessage(" " + pluginName);
                            sender.sendMessage(" §f§l플레이어 목록:");
                            if (SelonZombieSurvival.manager.getPlayerList().size() > 0) { // 게임에 참여하고 있는 플레이어 인원이 0명 초과일 경우
                                for(Player player : SelonZombieSurvival.manager.getPlayerList()) {
                                    sender.sendMessage(" §f- §7" + player.getName());
                                }
                            } else { // 게임에 참여하고 있는 플레이어 인원이 0명일 경우
                                sender.sendMessage(" §f- []");
                            }
                        } else if (args[1].equalsIgnoreCase("Add")) {
                            if (args.length > 2) {
                                Player player = Bukkit.getPlayer(args[2]);
                                if (player != null && player.isOnline()) { // 플레이어가 존재하고, 플레이어가 온라인일 경우
                                    if (SelonZombieSurvival.manager.addPlayer(player)) {
                                        sender.sendMessage(serverTitle + " §f§l해당 플레이어(§6§l" + player.getName() + "§f§l)를 게임에 참여시켰습니다!");
                                    } else {
                                        sender.sendMessage(serverTitle + " §f§l해당 플레이어(§6§l" + player.getName() + "§f§l)는 이미 게임에 참여하고 있습니다!");
                                    }
                                } else { // 플레이어가 온라인이 아닐 경우
                                    sender.sendMessage(serverTitle + " §f§l해당 플레이어(§6§l" + args[2] + "§f§l)를 찾을 수 없습니다!");
                                }
                            } else {
                                sender.sendMessage("");
                                sender.sendMessage(" " + pluginName);
                                sender.sendMessage(" §f§l/SelonZS Player Add §e§l<Player>§7§l: §f게임에 플레이어를 추가합니다.");
                            }
                        } else if (args[1].equalsIgnoreCase("Remove")) {
                            if (args.length > 2) {
                                Player player = Bukkit.getPlayer(args[2]);
                                if (player != null && player.isOnline()) { // 플레이어가 존재하고, 플레이어가 온라인일 경우
                                    if (SelonZombieSurvival.manager.removePlayer(player)) {
                                        sender.sendMessage(serverTitle + " §f§l해당 플레이어(§6§l" + player.getName() + "§f§l)를 게임에서 제거하였습니다!");
                                    } else {
                                        sender.sendMessage(serverTitle + " §f§l해당 플레이어(§6§l" + player.getName() + "§f§l)는 이미 게임에 참여하고 있지 않습니다!");
                                    }
                                } else { // 플레이어가 온라인이 아닐 경우
                                    sender.sendMessage(serverTitle + " §f§l해당 플레이어(§6§l" + args[2] + "§f§l)를 찾을 수 없습니다!");
                                }
                            } else {
                                sender.sendMessage("");
                                sender.sendMessage(" " + pluginName);
                                sender.sendMessage(" §f§l/SelonZS Player Remove §e§l<Player>§7§l: §f게임에서 플레이어를 제거합니다.");
                            }
                        }
                    } else {
                        sender.sendMessage("");
                        sender.sendMessage(" " + pluginName);
                        sender.sendMessage(" §f§l/SelonZS Player List§7§l: §f게임에 참여하고 있는 플레이어 목록을 확인합니다.");
                        sender.sendMessage(" §f§l/SelonZS Player Add §e§l<Player>§7§l: §f게임에 플레이어를 추가합니다.");
                        sender.sendMessage(" §f§l/SelonZS Player Remove §e§l<Player>§7§l: §f게임에서 플레이어를 제거합니다.");
                    }

                } else if (args[0].equalsIgnoreCase("Join")) {
                    if (SelonZombieSurvival.manager.addPlayer((Player) sender)) {
                        sender.sendMessage(serverTitle + " §f§l좀비 서바이벌 게임에 참여합니다!");
                    } else { // 플레이어가 이미 게임에 참여하고 있을 경우
                        sender.sendMessage(serverTitle + " §f§l플레이어는 이미 게임에 참여하고 있습니다!");
                    }

                } else if (args[0].equalsIgnoreCase("Leave")) {
                    Player player = (Player)sender;
                    Manager manager = SelonZombieSurvival.manager;
                    if (manager.removePlayer(player)) {
                        if (manager.getGameProgress() == Manager.GameProgress.GAME) { // 게임 중일 경우
                            // PlayerEscapeEvent 이벤트 호출
                            PlayerEscapeEvent pEvent = new PlayerEscapeEvent(player);
                            Bukkit.getServer().getPluginManager().callEvent(pEvent);

                            manager.escapePenalty(pEvent.getPlayer()); // 탈주 패널티 함수 호출
                        }
                        sender.sendMessage(serverTitle + " §f§l좀비 서바이벌 게임에서 제외됩니다!");
                    } else { // 플레이어가 이미 게임에 참여하고 있지 않을 경우
                        sender.sendMessage(serverTitle + " §f§l플레이어는 이미 게임에 참여하고 있지 않습니다!");
                    }

                }

            } else {
                sender.sendMessage("");
                sender.sendMessage(" " + pluginName);
                sender.sendMessage(" §f§l/SelonZS Load§7§l: §f플러그인을 불러옵니다.");
                sender.sendMessage(" §f§l/SelonZS Save§7§l: §f플러그인을 저장합니다.");
                sender.sendMessage(" §f§l/SelonZS Reload§7§l: §f플러그인을 리로드합니다.");
                sender.sendMessage(" §f§l/SelonZS Lobby Set§7§l: §f플레이어의 위치를 로비 위치로 설정합니다.");
                sender.sendMessage(" §f§l/SelonZS Map List§7§l: §f맵 목록을 확인합니다.");
                sender.sendMessage(" §f§l/SelonZS Map Add §e§l<Map>§7§l: §f맵을 추가합니다.");
                sender.sendMessage(" §f§l/SelonZS Map Remove §e§l<Map>§7§l: §f맵을 제거합니다.");
                sender.sendMessage(" §f§l/SelonZS Map Select §e§l<Map>§7§l: §f맵을 선택합니다.");
                sender.sendMessage(" §f§l/SelonZS MapOption §e§l<Map> §f§lLocation List§7§l: §f맵 위치를 확인합니다.");
                sender.sendMessage(" §f§l/SelonZS MapOption §e§l<Map> §f§lLocation Add§7§l: §f맵 위치를 추가합니다.");
                sender.sendMessage(" §f§l/SelonZS MapOption §e§l<Map> §f§lLocation Remove §e§l<Number>§7§l: §f맵 위치를 제거합니다.");
                sender.sendMessage(" §f§l/SelonZS MapOption §e§l<Map> §f§lSupply Location List§7§l: §f보급 위치를 확인합니다.");
                sender.sendMessage(" §f§l/SelonZS MapOption §e§l<Map> §f§lSupply Location Add§7§l: §f보급 위치를 추가합니다.");
                sender.sendMessage(" §f§l/SelonZS MapOption §e§l<Map> §f§lSupply Location Remove §e§l<Number>§7§l: §f...");
                sender.sendMessage(" §f§l/SelonZS Game Start§7§l: §f좀비 서바이벌 게임을 시작합니다.");
                sender.sendMessage(" §f§l/SelonZS Game Stop§7§l: §f좀비 서바이벌 게임을 중지합니다.");
                sender.sendMessage(" §f§l/SelonZS Player List§7§l: §f게임에 참여하고 있는 플레이어 목록을 확인합니다.");
                sender.sendMessage(" §f§l/SelonZS Player Add §e§l<Player>§7§l: §f게임에 플레이어를 추가합니다.");
                sender.sendMessage(" §f§l/SelonZS Player Remove §e§l<Player>§7§l: §f게임에서 플레이어를 제거합니다.");
                sender.sendMessage(" §f§l/SelonZS Join§7§l: §f좀비 서바이벌 게임에 참여합니다.");
                sender.sendMessage(" §f§l/SelonZS Leave§7§l: §f좀비 서바이벌 게임에서 퇴장합니다.");
            }

        } else if (sender.hasPermission("selonzs.user")) { // 유저 권한일 경우

            if (args.length > 0) {

                if (args[0].equalsIgnoreCase("Join")) {
                    if (SelonZombieSurvival.manager.addPlayer((Player) sender)) {
                        sender.sendMessage(serverTitle + " §f§l좀비 서바이벌 게임에 참여합니다!");
                    } else { // 플레이어가 이미 게임에 참여하고 있을 경우
                        sender.sendMessage(serverTitle + " §f§l플레이어는 이미 게임에 참여하고 있습니다!");
                    }

                } else if (args[0].equalsIgnoreCase("Leave")) {
                    Player player = (Player)sender;
                    Manager manager = SelonZombieSurvival.manager;
                    if (manager.removePlayer(player)) {
                        if (manager.getGameProgress() == Manager.GameProgress.GAME) { // 게임 중일 경우
                            // PlayerEscapeEvent 이벤트 호출
                            PlayerEscapeEvent pEvent = new PlayerEscapeEvent(player);
                            Bukkit.getServer().getPluginManager().callEvent(pEvent);

                            manager.escapePenalty(pEvent.getPlayer()); // 탈주 패널티 함수 호출
                        }
                        sender.sendMessage(serverTitle + " §f§l좀비 서바이벌 게임에서 제외됩니다!");
                    } else { // 플레이어가 이미 게임에 참여하고 있지 않을 경우
                        sender.sendMessage(serverTitle + " §f§l플레이어는 이미 게임에 참여하고 있지 않습니다!");
                    }

                }

            } else {
                sender.sendMessage("");
                sender.sendMessage(" " + pluginName);
                sender.sendMessage(" §f§l/SelonZS Join§7§l: §f좀비 서바이벌 게임에 참여합니다.");
                sender.sendMessage(" §f§l/SelonZS Leave§7§l: §f좀비 서바이벌 게임에서 퇴장합니다.");
            }

        }

        return false;
    }

}