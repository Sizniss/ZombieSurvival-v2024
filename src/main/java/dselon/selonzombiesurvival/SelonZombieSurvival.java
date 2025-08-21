package dselon.selonzombiesurvival;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.event.EventHandler;
import org.bukkit.plugin.java.JavaPlugin;

public final class SelonZombieSurvival extends JavaPlugin {

    public static SelonZombieSurvival plugin;
    public static Files files;
    public static Manager manager;

    private Metrics metrics;

    @Override
    public void onEnable() {
        // Plugin startup logic
        plugin = this;

        // metrics = new Metrics(this, 18121); // bStats 등록

        files = new Files(); // 파일 객체 생성
        manager = new Manager(); // 매니저 객체 생성
        Bukkit.getPluginManager().registerEvents(new Events(), plugin); // 이벤트 등록
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        
        // files.saveConfig(); // 콘피그 저장
        // files.saveData(); // 데이터 저장
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args)
    {
        if (command.getName().equalsIgnoreCase("SelonZS"))
        {
            return new Commands().onCommand(sender, command, label, args);
        }
        return false;
    }

}
