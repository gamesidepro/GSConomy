package gsconomy;

import com.google.common.collect.Lists;
import com.google.inject.Inject;
import events.MainEventClass;
import executors.InfoExecutor;
import executors.PayExecutor;
import java.io.File;
import java.util.logging.Logger;
import ninja.leaping.configurate.commented.CommentedConfigurationNode;
import ninja.leaping.configurate.loader.ConfigurationLoader;
import org.spongepowered.api.Game;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.config.DefaultConfig;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.game.state.GameInitializationEvent;
import org.spongepowered.api.event.game.state.GamePreInitializationEvent;
import org.spongepowered.api.event.game.state.GameStartedServerEvent;
import org.spongepowered.api.plugin.Plugin;
import org.spongepowered.api.text.Text;

/**
 * @website gameside.pro
 * @author Twelvee
 */
@Plugin(id = "gsconomy", name = "[GS] iConomy", version = "1.2", description = "iConomy for 1.11 sponge and GAMESIDE DOT PRO")
public class GSConomy {
    public static GSConomy instance;
    private DataBase db;
	@Inject
	Game game;
	
	@Inject
	@DefaultConfig(sharedRoot = false)
	private File configFile;

	@Inject
	@DefaultConfig(sharedRoot = false)
	private ConfigurationLoader<CommentedConfigurationNode> configManager;

	@Inject
	private Logger logger;


	public File getConfigFile() {
		return configFile;
	}

	public ConfigurationLoader<CommentedConfigurationNode> getConfigManager() {
		return configManager;
	}

	public Logger getLogger() {
		return logger;
	}
        
        public DataBase getDb(){
            return db;
        }

	@Listener
	public void onPreInit(GamePreInitializationEvent event) {
	    instance = this;
	    logger.info("Loading GameSide iConomy plugin...");
	}
    	@Listener
	public void onInit(GameInitializationEvent event) {
		ConfigurationManager.getInstance().setup(configFile, configManager);
                
		CommandSpec InfoExecutor = CommandSpec.builder().description(Text.of("Информация о балансе"))
				.executor(new InfoExecutor())
				.build();

		CommandSpec PayExecutor = CommandSpec.builder().description(Text.of("Перевести деньги"))
                                .arguments(GenericArguments.optional(GenericArguments.string(Text.of("name"))), GenericArguments.optional(GenericArguments.integer((Text.of("paymoney")))))
				.executor(new PayExecutor())
				.build();
                
                Sponge.getCommandManager().register(this, InfoExecutor, Lists.newArrayList("money", "balance"));
                Sponge.getCommandManager().register(this, PayExecutor, Lists.newArrayList("pay", "перевод"));
                logger.info("[GS] iConomy loaded.");
                game.getEventManager().registerListeners(this, new MainEventClass());
                db = new DataBase();
                db.connect();

        }
        
	
	@Listener
	public void onStart(GameStartedServerEvent event) {
		logger.info("[GS] iConomy loaded.");
	}
}
