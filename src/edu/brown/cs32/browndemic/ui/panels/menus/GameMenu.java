package edu.brown.cs32.browndemic.ui.panels.menus;

import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BoxLayout;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.Timer;
import javax.swing.UIManager;

import edu.brown.cs32.browndemic.ui.BrowndemicFrame;
import edu.brown.cs32.browndemic.ui.DumbChatServer;
import edu.brown.cs32.browndemic.ui.Resources;
import edu.brown.cs32.browndemic.ui.UIConstants.Colors;
import edu.brown.cs32.browndemic.ui.UIConstants.Fonts;
import edu.brown.cs32.browndemic.ui.UIConstants.Images;
import edu.brown.cs32.browndemic.ui.UIConstants.Strings;
import edu.brown.cs32.browndemic.ui.Utils;
import edu.brown.cs32.browndemic.ui.actions.Action;
import edu.brown.cs32.browndemic.ui.components.WorldMap;
import edu.brown.cs32.browndemic.ui.panels.UIPanel;
import edu.brown.cs32.browndemic.ui.panels.subpanels.ChatPanel;
import edu.brown.cs32.browndemic.ui.panels.subpanels.InformationBar;
import edu.brown.cs32.browndemic.ui.panels.subpanels.NewsPanel;
import edu.brown.cs32.browndemic.ui.panels.subpanels.RegionPanel;
import edu.brown.cs32.browndemic.ui.panels.subpanels.StatPanel;
import edu.brown.cs32.browndemic.ui.panels.subpanels.UpgradePanel;
import edu.brown.cs32.browndemic.ui.panels.titlebars.InGameTitleBar;
import edu.brown.cs32.browndemic.world.World;

public class GameMenu extends UIPanel {
	
	private static final long serialVersionUID = 3275157554958820602L;
	
	private World _world;
	private WorldMap _map;
	private boolean _loaded = false, _multiplayer;
	private int _disease;
	private InformationBar _info;
	private ChatPanel _chat;
	private NewsPanel _news;
	private RegionPanel _regions;
	private StatPanel _stats;
	private UpgradePanel _upgrade;
	
	public GameMenu(World w, int disease, boolean multiplayer) {
		super();
		_world = w;
		_disease = disease;
		_multiplayer = multiplayer;
	}
	
	private class ImagesDoneLoadingAction implements Action {
		BrowndemicFrame _parent;
		public ImagesDoneLoadingAction(BrowndemicFrame parent) {
			_parent = parent;
		}
		@Override
		public void doAction() {
			_map = new WorldMap(_world, Resources.getImage(Images.MAP), Resources.getImage(Images.REGIONS), _disease);
			_parent.setPanel(new Loading(true, _map.new Loader(new RegionsDoneLoadingAction(_parent))));
		}
	}
	
	private class RegionsDoneLoadingAction implements Action {
		BrowndemicFrame _parent;
		public RegionsDoneLoadingAction(BrowndemicFrame parent) {
			_parent = parent;
		}
		@Override
		public void doAction() {
			makeUI();
			_loaded = true;
			_parent.setPanel(GameMenu.this);
		}
	}
	
	@Override
	protected void makeUI() {
		super.makeUI();
		
		add(_info = new InformationBar(_world, _disease));
		
		add(_map);
		
		JPanel bottom = new JPanel();
		bottom.setBackground(Colors.TRANSPARENT);
		bottom.setOpaque(false);
		bottom.setLayout(new BoxLayout(bottom, BoxLayout.X_AXIS));
		
		bottom.add(_upgrade = new UpgradePanel(_world.getDiseases().get(_disease)));

		UIManager.put("TabbedPane.selected", Colors.MENU_BACKGROUND);
		UIManager.put("TabbedPane.focus", Colors.MENU_BACKGROUND);
		UIManager.put("TabbedPane.selectHighlight", Colors.MENU_BACKGROUND);
		UIManager.put("TabbedPane.shadow", Colors.MENU_BACKGROUND);
		UIManager.put("TabbedPane.darkShadow", Colors.MENU_BACKGROUND);
		UIManager.put("TabbedPane.selected", Colors.MENU_BACKGROUND);
		UIManager.put("TabbedPane.borderHighlightColor", Colors.MENU_BACKGROUND);
		UIManager.put("TabbedPane.background", Colors.MENU_BACKGROUND);
		UIManager.put("TabbedPane.unselectedBackground", Colors.MENU_BACKGROUND);
		UIManager.put("TabbedPane.light", Colors.RED_TEXT);
		UIManager.put("TabbedPane.contentBorderInsets", new Insets(0, 0, 0, 0));
		
		
		JTabbedPane botRight = new JTabbedPane();
		Utils.setDefaultLook(botRight);
		
		botRight.setForeground(Colors.RED_TEXT);
		botRight.setFont(Fonts.TITLE_BAR);
		if (_multiplayer) {
			botRight.addTab("Chat", _chat = new ChatPanel(new DumbChatServer()));
		}
		botRight.addTab("News", _news = new NewsPanel(_world));
		botRight.addTab("Stats", _stats = new StatPanel(_world.getDiseases().get(_disease)));
		botRight.addTab("Regions", _regions =  new RegionPanel(_world));
		for (int i = 0; i < botRight.getTabCount(); i++) {
			botRight.setBackgroundAt(i, Colors.MENU_BACKGROUND);
		}
		
		bottom.add(botRight);
		
		add(bottom);
	}

	@Override
	public void setupForDisplay() {
		if (_loaded) {
			Utils.getParentFrame(this).setTitle(new InGameTitleBar(_world, true));
			new Timer(3000, new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent arg0) {
					_map.addRandomPlane();
				}
			}).start();
			_map.setChooseMode(true);
		} else {
			Utils.getParentFrame(this).setPanel(new Loading(true, new Loading.LoadImageWorker(new ImagesDoneLoadingAction(Utils.getParentFrame(this)), Images.GAME_IMAGES)));
		}
	}
	
	@Override
	public String toString() {
		return Strings.SINGLEPLAYER_GAME;
	}
	
	private void stop() {
		_news.stop();
		_info.stop();
		_regions.stop();
		_stats.stop();
		_upgrade.stop();
	}
        
    @Override
    public void stopPanel() {
        stop();
    }
	
}
