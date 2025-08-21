package dselon.selonzombiesurvival;

import dselon.selonzombiesurvival.customevents.PlayerEscapeEvent;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.*;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryInteractEvent;
import org.bukkit.event.player.*;
import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.event.server.PluginEnableEvent;

import java.util.ArrayList;

public class Events implements Listener {

    // 플러그인 활성화 이벤트
    @EventHandler
    private void PluginEnableEvent(PluginEnableEvent event) {
        if (event.getPlugin().getName().equals("SelonZombieSurvival")) {
            Manager manager = SelonZombieSurvival.manager;

            // 게임 자동 시작 설정
            boolean autoStart = Files.getAutoStart();
            if (autoStart) { // 게임 자동 시작이 활성화되어 있을 경우
                manager.gameStart(); // 게임 시작
            }
        }
    }

    // 플러그인 비활성화 이벤트
    @EventHandler
    private void PluginDisableEvent(PluginDisableEvent event) {
        if (event.getPlugin().getName().equals("SelonZombieSurvival")) {
            Manager manager = SelonZombieSurvival.manager;

            manager.gameStop();// 게임 종료
            manager.initPlayerList(); // 참여 플레이어 목록 초기화
        }
    }

    // 플레이어가 아이템을 떨구는 이벤트
    @EventHandler
    private void PlayerDropItemEvent(PlayerDropItemEvent event) {
        Manager manager = SelonZombieSurvival.manager;
        if (manager.getPlayerList().contains(event.getPlayer())) { // 플레이어가 게임 참여 중일 경우
            // 게임 진행 상태가 준비 중 또는 게임 중 또는 종료 중일 경우
            Manager.GameProgress gameProgress = manager.getGameProgress();
            if (gameProgress == Manager.GameProgress.READY || gameProgress == Manager.GameProgress.GAME || gameProgress == Manager.GameProgress.ENDING) {
                event.setCancelled(true); // 이벤트 취소
            }
        }
    }

    // 엔티티 아이템 줍기 이벤트
    @EventHandler
    private void EntityPickupItemEvent(EntityPickupItemEvent event) {
        if (event.getEntityType() != EntityType.PLAYER) { // 엔티티 타입이 플레이어가 아닐 경우
            return;
        }

        Player player = (Player)event.getEntity();
        Manager manager = SelonZombieSurvival.manager;
        if (manager.getPlayerList().contains(player)) { // 플레이어가 게임 참여 중일 경우
            // 게임 진행 상태가 준비 중 또는 게임 중 또는 종료 중일 경우
            Manager.GameProgress gameProgress = manager.getGameProgress();
            if (gameProgress == Manager.GameProgress.READY || gameProgress == Manager.GameProgress.GAME || gameProgress == Manager.GameProgress.ENDING) {
                event.setCancelled(true); // 이벤트 취소
            }
        }
    }

    // 인벤토리 클릭 이벤트
    @EventHandler
    private void InventoryClickEvent(InventoryClickEvent event) {
        Manager manager = SelonZombieSurvival.manager;
        if (manager.getPlayerList().contains(event.getWhoClicked())) { // 플레이어가 게임 참여 중일 경우
            // 게임 진행 상태가 준비 중 또는 게임 중 또는 종료 중일 경우
            Manager.GameProgress gameProgress = manager.getGameProgress();
            if (gameProgress == Manager.GameProgress.READY || gameProgress == Manager.GameProgress.GAME || gameProgress == Manager.GameProgress.ENDING) {
                event.setCancelled(true); // 이벤트 취소
            }
        }
    }

    // 인벤토리 드래그 이벤트
    @EventHandler
    private void InventoryDragEvent(InventoryDragEvent event) {
        Manager manager = SelonZombieSurvival.manager;
        if (manager.getPlayerList().contains(event.getWhoClicked())) { // 플레이어가 게임 참여 중일 경우
            // 게임 진행 상태가 준비 중 또는 게임 중 또는 종료 중일 경우
            Manager.GameProgress gameProgress = manager.getGameProgress();
            if (gameProgress == Manager.GameProgress.READY || gameProgress == Manager.GameProgress.GAME || gameProgress == Manager.GameProgress.ENDING) {
                event.setCancelled(true); // 이벤트 취소
            }
        }
    }

    // 플레이어 손 바꾸기 이벤트
    @EventHandler
    private void PlayerSwapHandItemEvent(PlayerSwapHandItemsEvent event) {
        Manager manager = SelonZombieSurvival.manager;
        if (manager.getPlayerList().contains(event.getPlayer())) { // 플레이어가 게임 참여 중일 경우
            // 게임 진행 상태가 준비 중 또는 게임 중 또는 종료 중일 경우
            Manager.GameProgress gameProgress = manager.getGameProgress();
            if (gameProgress == Manager.GameProgress.READY || gameProgress == Manager.GameProgress.GAME || gameProgress == Manager.GameProgress.ENDING) {
                event.setCancelled(true); // 이벤트 취소
            }
        }
    }

    // 허기 레벨 변경 이벤트
    @EventHandler
    private void FoodLevelChangeEvent(FoodLevelChangeEvent event) {
        if (event.getEntityType() != EntityType.PLAYER) { // 엔티티 타입이 플레이어가 아닐 경우
            return;
        }

        Player player = (Player)event.getEntity();
        Manager manager = SelonZombieSurvival.manager;
        if (manager.getPlayerList().contains(player)) { // 플레이어가 게임 참여 중일 경우
            // 게임 진행 상태가 준비 중 또는 게임 중 또는 종료 중일 경우
            Manager.GameProgress gameProgress = manager.getGameProgress();
            if (gameProgress == Manager.GameProgress.READY || gameProgress == Manager.GameProgress.GAME || gameProgress == Manager.GameProgress.ENDING) {
                event.setCancelled(true); // 이벤트 취소
            }
        }
    }

    // 플레이어 서버 퇴장 이벤트
    @EventHandler
    private void PlayerQuitEvent(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        Manager manager = SelonZombieSurvival.manager;
        if (manager.removePlayer(player)) {
            if (manager.getGameProgress() == Manager.GameProgress.GAME) { // 게임 중일 경우
                // PlayerEscapeEvent 이벤트 호출
                PlayerEscapeEvent pEvent = new PlayerEscapeEvent(player);
                Bukkit.getServer().getPluginManager().callEvent(pEvent);

                manager.escapePenalty(pEvent.getPlayer()); // 탈주 패널티 함수 호출
            }
        }
    }
    
    // 플레이어 사망 이벤트
    @EventHandler
    private void PlayerDeathEvent(PlayerDeathEvent event) {
        Player player = event.getEntity();
        Manager manager = SelonZombieSurvival.manager;
        if (manager.getPlayerList().contains(player)) { // 플레이어가 게임 참여 중일 경우
            Manager.GameProgress gameProgress = manager.getGameProgress();
            switch (gameProgress) {
                case READY: // 게임 진행 상태가 준비 중일 경우
                    event.setDeathMessage("");
                    event.setKeepInventory(true);
                    event.setKeepLevel(true);
                    event.setDroppedExp(0);
                    break;

                case GAME: // 게임 진행 상태가 게임 중일 경우
                    event.setDeathMessage("");
                    event.setKeepInventory(true);
                    event.setKeepLevel(true);
                    event.setDroppedExp(0);

                    Player killer = player.getKiller();
                    if (manager.getHumanList().contains(player)) { // 플레이어가 인간일 경우
                        if (killer == null) { // 공격자가 없을 경우
                            manager.infected(player); // 감염 함수 호출
                        } else { // 공격자가 있을 경우
                            if (!manager.getZombieList().contains(killer)) { // 공격자가 좀비가 아닐 경우
                                manager.infected(player); // 감염 함수 호출
                            } else { // 공격자가 좀비일 경우
                                manager.infected(player, killer); // 감염 함수 호출
                            }
                        }

                    } else if (manager.getZombieList().contains(player)) { // 플레이어가 좀비일 경우
                        if (killer == null) { // 공격자가 없을 경우
                            manager.killed(player); // 사망 함수 호출
                        } else { // 공격자가 있을 경우
                            if (!manager.getHumanList().contains(killer)) { // 공격자가 인간이 아닐 경우
                                manager.killed(player); // 사망 함수 호출
                            } else { // 공격자가 인간일 경우
                                manager.killed(player, killer); // 사망 함수 호출
                            }
                        }

                    }
                    break;

                case ENDING: // 게임 진행 상태가 종료 중일 경우
                    event.setDeathMessage("");
                    event.setKeepInventory(true);
                    event.setKeepLevel(true);
                    event.setDroppedExp(0);
                    break;
            }
        }
    }
    
    // 플레이어 리스폰 이벤트
    @EventHandler
    private void PlayerRespawnEvent(PlayerRespawnEvent event) {
        Player player = event.getPlayer();
        Manager manager = SelonZombieSurvival.manager;
        if (manager.getPlayerList().contains(player)) { // 플레이어가 게임 참여 중일 경우
            Manager.GameProgress gameProgress = manager.getGameProgress();
            Location lobbyLocation = manager.getLobbyLocation();
            ArrayList<Location> mapLocation = manager.getMapLocation();
            int randomNumber = (int)(Math.random() * mapLocation.size());
            switch (gameProgress) {
                case IDLE:
                    event.setRespawnLocation(lobbyLocation);
                    break;

                case WAITING:
                    event.setRespawnLocation(lobbyLocation);
                    break;

                case READY:
                    event.setRespawnLocation(mapLocation.get(randomNumber));
                    if (manager.getHumanList().contains(player)) { // 플레이어가 인간일 경우
                        manager.convertHuman(player); // 플레이어를 인간으로 변환
                    }
                    break;

                case GAME:
                    event.setRespawnLocation(mapLocation.get(randomNumber));
                    if (manager.getHumanList().contains(player)) { // 플레이어가 인간일 경우
                        manager.convertHuman(player); // 인간 변환 함수 호출
                        if (manager.getHeroList().contains(player)) { // 플레이어가 영웅일 경우
                            manager.convertHero(player); // 영웅 변환 함수 호출
                        }

                    } else if (manager.getZombieList().contains(player)) { // 플레이어가 좀비일 경우
                        manager.convertZombie(player); // 좀비 변환 함수 호출
                        if (manager.getHostList().contains(player)) { // 플레이어가 숙주일 경우
                            manager.convertHost(player); // 숙주 변환 함수 호출
                        } else if (manager.getCarrierList().contains(player)) { // 플레이어가 보균자일 경우
                            manager.convertCarrier(player); // 보균자 변환 함수 호출
                        }
                    }
                    break;

                case ENDING:
                    event.setRespawnLocation(mapLocation.get(randomNumber));
                    break;

                default:
                    event.setRespawnLocation(lobbyLocation);
                    break;
            }
        }
    }

    // 플레이어 상호작용 이벤트
    @EventHandler
    private void PlayerInteractEvent(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        Manager manager = SelonZombieSurvival.manager;
        if (manager.getPlayerList().contains(player)) { // 플레이어가 게임 참여 중일 경우
            if (manager.getHumanList().contains(player)) { // 플레이어가 인간일 경우
                if (event.getAction() == Action.RIGHT_CLICK_BLOCK) { // 블럭을 우클릭 했을 경우
                    Block block = event.getClickedBlock();
                    if (manager.getSupplyLocation().contains(block.getLocation())) { // 클릭한 블럭의 위치가 보급 위치일 경우
                        event.setCancelled(true); // 이벤트 취소
                        manager.acquiredSupply(player, block); // 보급 획득
                    }
                }
            } else if (manager.getZombieList().contains(player)) { // 플레이어가 좀비일 경우
                if (event.getAction() == Action.RIGHT_CLICK_BLOCK) { // 블럭을 우클릭 했을 경우
                    Block block = event.getClickedBlock();
                    if (manager.getSupplyLocation().contains(block.getLocation())) { // 클릭한 블럭의 위치가 보급 위치일 경우
                        event.setCancelled(true); // 이벤트 취소
                    }
                }
            }
        }
    }
}
