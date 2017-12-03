package executors;

import gsconomy.ConfigurationManager;
import gsconomy.GSConomy;
import java.sql.SQLException;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.format.TextColors;

public class PayExecutor implements CommandExecutor{
    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
        if(src instanceof Player) {
            Player player = (Player)src;
            int userId = -1;
            float money = 0;
            float paymoney = -1;
            String table = ConfigurationManager.getInstance().getConfig().getNode("balance_table").getString();
            String name = "";
            if(args.hasAny("name")) {
                name = args.<String>getOne("name").get();
            }
            if(args.hasAny("paymoney")) {
                paymoney = args.<Integer>getOne("paymoney").get();
            }
            try {
                userId = GSConomy.instance.getDb().getUserId("xf_user", player.getName());
            } catch (SQLException ex) {
                Logger.getLogger(InfoExecutor.class.getName()).log(Level.SEVERE, null, ex);
            }
            if(userId!=-1){
                
                try {
                    money = GSConomy.instance.getDb().getUserBalance(table, userId);
                } catch (SQLException ex) {
                    Logger.getLogger(InfoExecutor.class.getName()).log(Level.SEVERE, null, ex);
                }
                if(money>=paymoney){
                    try {
                        int otherUserId = GSConomy.instance.getDb().getUserId("xf_user", name);
                        // Проверка другого игрока
                        if(otherUserId!=-1){
                            if(userId!=otherUserId){
                                GSConomy.instance.getDb().getResultSet("UPDATE `"+table+"` SET `ms`=`ms`+'"+paymoney+"' WHERE `user_id`='"+otherUserId+"'");
                                GSConomy.instance.getDb().getResultSet("UPDATE `"+table+"` SET `ms`=`ms`-'"+paymoney+"' WHERE `user_id`='"+userId+"'");
                                utils.Utils.sendMessage(player, TextColors.GREEN, "[Игровая Сторона] ",TextColors.WHITE,"Вы успешно перевели ",TextColors.GOLD, paymoney+" мс. ",TextColors.WHITE,"на счет игрока ",TextColors.GOLD, name);                                
                                Optional<Player> opla = Sponge.getServer().getPlayer(name);
                                //utils.Utils.sendMessage(player, opla.toString());
                                if(!opla.toString().equals("Optional.empty")){
                                    if(Sponge.getServer().getOnlinePlayers().contains(Sponge.getServer().getPlayer(name).get())){
                                        Player otherPlayer = Sponge.getServer().getPlayer(name).get();
                                        utils.Utils.sendMessage(otherPlayer, TextColors.GREEN, "[Игровая Сторона] ",TextColors.WHITE,"Вы получили ",TextColors.GOLD, paymoney+" мс. ",TextColors.WHITE,"от игрока ",TextColors.GOLD, player.getName()); 
                                    }   
                                }
                            }
                        }else{
                            utils.Utils.sendMessage(player, TextColors.RED, "[Игровая Сторона] ",TextColors.WHITE,"Данный игрок не найден.");
                        }
                    } catch (SQLException ex) {
                        Logger.getLogger(PayExecutor.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }else{
                    utils.Utils.sendMessage(player, TextColors.RED, "[Игровая Сторона] ",TextColors.WHITE,"У Вас недостаточно денег.");
                }
            }else{
                utils.Utils.sendMessage(player, TextColors.RED, "[Игровая Сторона] ",TextColors.WHITE,"Данный игрок не найден.");
            }         
        }
        return CommandResult.success();
    }
}
