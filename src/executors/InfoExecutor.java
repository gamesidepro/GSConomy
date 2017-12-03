package executors;

import gsconomy.ConfigurationManager;
import gsconomy.GSConomy;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.format.TextColors;

public class InfoExecutor implements CommandExecutor{

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
        if(src instanceof Player) {
            Player player = (Player)src;
            int userId = -1;
            float money = 0;
            try {
                userId = GSConomy.instance.getDb().getUserId("xf_user", player.getName());
            } catch (SQLException ex) {
                Logger.getLogger(InfoExecutor.class.getName()).log(Level.SEVERE, null, ex);
            }
            if(userId!=-1){
                String table = ConfigurationManager.getInstance().getConfig().getNode("balance_table").getString();
                try {
                    money = GSConomy.instance.getDb().getUserBalance(table, userId);
                } catch (SQLException ex) {
                    Logger.getLogger(InfoExecutor.class.getName()).log(Level.SEVERE, null, ex);
                }
                utils.Utils.sendMessage(player, TextColors.GOLD, "[Игровая Сторона] ", TextColors.WHITE, "Ваш баланс: ", TextColors.GOLD, money+" мс.");
            }
            
        }
        return CommandResult.success();
    }
    
}
