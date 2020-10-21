package net.runelite.client.plugins.inventorymarkers;


import com.google.common.collect.ImmutableList;
import com.google.inject.Provides;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.*;
import net.runelite.api.events.*;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.events.ConfigChanged;
import net.runelite.client.menus.MenuManager;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.plugins.inventorymarkers.math.DuelSimulator;
import net.runelite.client.plugins.inventorymarkers.math.RSPlayer;
import net.runelite.client.plugins.inventorymarkers.ui.OddsPluginPanel;
import net.runelite.client.plugins.inventorymarkers.ui.PlayerOddsPanel;
import net.runelite.client.ui.ClientToolbar;
import net.runelite.client.ui.NavigationButton;
import net.runelite.client.ui.overlay.OverlayManager;
import net.runelite.client.util.ImageUtil;
import net.runelite.client.util.Text;
import net.runelite.http.api.hiscore.HiscoreClient;
import okhttp3.OkHttpClient;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.swing.*;
import java.awt.image.BufferedImage;
import java.util.concurrent.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
@PluginDescriptor(name = "Staking Odds", description = "Overlays to provide staking odds", enabledByDefault = false,
        tags = {"nomscripts"})
public class OddsPlugin extends Plugin
{
   @Inject
   private Client client;
   @Inject
   private OverlayManager overlayManager;
//   @Inject
//   private OddsOverlay overlay;
   @Inject
   private OddsConfig config;
   @Getter
   @Inject
   private ScheduledExecutorService executorService;

   @Inject
   private ClientThread clientThread;
   @Inject
   private ClientToolbar clientToolbar;
   private NavigationButton navigationButton;
   private OddsPluginPanel pluginPanel;
   @Getter
   private CopyOnWriteArrayList<PlayerOddsPanel> panels = new CopyOnWriteArrayList<>();

   @Inject
   private HiscoreClient hiscoreClient;

   @Inject
   private Provider<MenuManager> menuManager;
   private static final String LOOKUP = "Stake Odds";

   public OddsPlugin() {
   }

   @Provides
   OddsConfig getConfig(final ConfigManager configManager) {
      return (OddsConfig)configManager.getConfig((Class)OddsConfig.class);
   }


   @Provides
   HiscoreClient provideHiscoreClient(OkHttpClient okHttpClient)
   {
      return new HiscoreClient(okHttpClient);
   }

   protected void startUp() {
//      this.overlayManager.add((Overlay)this.overlay);

      pluginPanel = new OddsPluginPanel(this, config);
      final String ICON_FILE = "panel_icon.png";
      final BufferedImage icon = ImageUtil.getResourceStreamFromClass(getClass(), ICON_FILE);
//
      navigationButton = NavigationButton.builder()
              .tooltip("Odds calc")
              .icon(icon)
              .priority(5)
              .panel(pluginPanel)
              .build();

      clientToolbar.addNavigation(navigationButton);

      if (config.playerOption() && client != null)
      {
         menuManager.get().addPlayerMenuItem(LOOKUP);
      }
   }

   protected void shutDown() {
//      this.overlayManager.remove((Overlay)this.overlay);
      clientToolbar.removeNavigation(navigationButton);

      if (client != null)
      {
         menuManager.get().removePlayerMenuItem(LOOKUP);
      }
   }



   @Subscribe
   public void onPlayerMenuOptionClicked(PlayerMenuOptionClicked event)
   {
      log.info(event.getMenuOption() + " " + Text.removeTags(event.getMenuTarget()));
      if (event.getMenuOption().equals(LOOKUP) || "Challenge".equals(event.getMenuOption()))
      {
         addPanel(Text.removeTags(event.getMenuTarget()));
      }
   }

   Pattern pattern = Pattern.compile("^(.* {2})\\(.*");
   @Subscribe
   public void onMenuOptionClicked(MenuOptionClicked event)
   {
      if (!Text.removeTags(event.getMenuOption()).equals("Challenge"))
      {
         return;
      }
      Matcher m = pattern.matcher(Text.removeTags(event.getMenuTarget()));
      log.info("Pattern matching " + Text.removeTags(event.getMenuTarget()));
      if (m.find()) {
         addPanel(m.group(1).trim());
      }
   }

   @Subscribe
   public void onConfigChanged(ConfigChanged event)
   {
      if (event.getGroup().equals("odds"))
      {
         if (client != null)
         {
            menuManager.get().removePlayerMenuItem(LOOKUP);

            if (config.playerOption())
            {
               menuManager.get().addPlayerMenuItem(LOOKUP);
            }
         }
      }
   }

   @Subscribe
   public void onChatMessage(final ChatMessage event) {
      if (!config.duelRequest()) return;
      if (event.getType() == ChatMessageType.CHALREQ_TRADE && event.getMessage().toLowerCase().endsWith("duel with you.")) {
         final Pattern p = Pattern.compile(".*?(?=\\s+wishes)");
         final Matcher m = p.matcher(event.getMessage());
         if (m.find()) {
            final String user = m.group(0);
            if (this.config.challengeSound()) {
               this.client.playSoundEffect(3925);
            }
            addPanel(user);
         }
      }
   }


//   Actor lastActor = null;
   @Subscribe
   public void onGameTick(GameTick gameTick)
   {
      pluginPanel.rebuild();
//      Player p = client.getLocalPlayer();
//      if (p == null) return;
//      Actor p2 = p.getInteracting();
//      if ((!(p2 instanceof Player))) return;
//      if (lastActor != null && lastActor.equals(p2)) return;
//      lastActor = p2;
//      addPanel(p2.getName());
   }

   private void addPanel(String opponent) {
      if (panels.stream().anyMatch(p -> p.getOpponentName().equalsIgnoreCase(opponent))) return;
      log.info("Begin lookup " + opponent);
      executorService.execute(()->{
         RSPlayer self = RSPlayer.fromSelf(this.client);
         RSPlayer opp = RSPlayer.fromName(hiscoreClient, opponent);
         if (opp == null) {
            log.info("Opponent lookup failed " + opponent);
            return;
         }
         DuelSimulator ds = new DuelSimulator(config, self, opp);
         SwingUtilities.invokeLater(()-> {
            if (panels.stream().anyMatch(p -> p.getOpponentName().equalsIgnoreCase(opponent))) return;
            panels.add(0,new PlayerOddsPanel(this, config, ds));
         });
      });
   }
}
