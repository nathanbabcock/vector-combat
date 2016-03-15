package view;

import model.Game;
import model.Player;
import model.Sprite;
import model.characters.CharClass;
import model.characters.Team;
import network.GameClient;

import java.awt.*;

/**
 * Created by Nathan on 3/14/2016.
 */
public class Scoreboard {
    Game game;

    public boolean open;

    public static final int width = 700;
    public static final int height = 400;

    public Scoreboard(Game game) {
        this.game = game;
    }


    public static void drawPingIcon(Graphics2D g2, int bars) {
        final int size = 2;
        final int gap = 1;

        if (bars >= 4)
            g2.setColor(Color.GREEN);
        else if (bars >= 3)
            g2.setColor(Color.YELLOW);
        else
            g2.setColor(Color.RED);

        g2.fillRect(0, 3 * size, size, size);

        g2.fillRect(size + gap, 2 * size, size, 2 * size);

        if (bars >= 3)
            g2.fillRect(2 * (size + gap), 1 * size, size, 3 * size);

        if (bars >= 4)
            g2.fillRect(3 * (size + gap), 0 * size, size, 4 * size);
    }

    public void draw(Graphics2D g2) {
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        // Draw guides
//        g2.setColor(new Color(255, 0, 0, 128));
//        g2.fillRect(0, 0, width, height);

        final int col_margin = 30;
        final int col_width = (width - col_margin) / 2;

        // HEADERS
        final int header_height = 50;
        final int end_radius = 10;
        final int header_margin = 5;
        final int teamname_padding = 10;
        final Font teamname_font = GameClient.FONT_HEADING.deriveFont(30f);
        final Font score_font = GameClient.FONT_HEADING.deriveFont(70f);
        final Font score_shadow_font = GameClient.FONT_HEADING.deriveFont(73f);

        // Red team header
        Graphics2D redHeader = (Graphics2D) g2.create();
        redHeader.setColor(Color.RED);
        redHeader.fillRect(0, header_margin, col_width, header_height - 2 * header_margin); // Background
        redHeader.fillPolygon(new int[]{0, -end_radius, 0}, new int[]{header_margin, header_height / 2, header_height - header_margin}, 3); // End caps
        redHeader.setColor(Color.WHITE);
        redHeader.setFont(teamname_font);
        redHeader.drawString("RED", teamname_padding, (header_height + 22) / 2); // Team name
        String redScore = game.getScore(Team.RED) + "";
        redHeader.setColor(Color.BLACK);
        redHeader.setFont(score_shadow_font);
        redHeader.drawString(redScore, col_width - redHeader.getFontMetrics(score_font).stringWidth(redScore) - 1, header_height + 1); // Score shadow
        //redHeader.drawRect((int) (col_width - redHeader.getFontMetrics(score_font).stringWidth(redScore) - score_padding - 1), header_height + 1, redHeader.getFontMetrics(score_font).stringWidth(redScore), redHeader.getFontMetrics(score_font).getHeight());
        redHeader.setColor(Color.WHITE);
        redHeader.setFont(score_font);
        redHeader.drawString(redScore, col_width - redHeader.getFontMetrics().stringWidth(redScore), header_height); // Score

        // Blue team header
        Graphics2D blueHeader = (Graphics2D) g2.create();
        blueHeader.translate(col_width + col_margin, 0);
        blueHeader.setColor(Color.BLUE);
        blueHeader.fillRect(0, header_margin, col_width, header_height - 2 * header_margin); // Background
        blueHeader.fillPolygon(new int[]{col_width, col_width + end_radius, col_width}, new int[]{header_margin, header_height / 2, header_height - header_margin}, 3); // End caps
        blueHeader.setColor(Color.WHITE);
        blueHeader.setFont(teamname_font);
        blueHeader.drawString("BLUE", col_width - teamname_padding - blueHeader.getFontMetrics().stringWidth("BLUE"), (header_height + 22) / 2); // Team name
        String blueScore = game.getScore(Team.BLUE) + "";
        blueHeader.setColor(Color.BLACK);
        blueHeader.setFont(score_shadow_font);
        blueHeader.drawString(blueScore, 10 - 1, header_height + 1); // Score shadow
        blueHeader.setColor(Color.WHITE);
        blueHeader.setFont(score_font);
        blueHeader.drawString(blueScore, 10, header_height); // Score

        // TABLE HEADERS
        final int tableheader_margintop = 5;
        final int tableheader_marginbottom = 5;
        final int tableheader_height = 20;

        final int table_edgepadding = 10;
        final Font tableheader_font = GameClient.FONT_BOLD.deriveFont(11f);

        final int tableheader_paddingbottom = 5;
        final int ping_width = 50; //g2.getFontMetrics(table_font).stringWidth("9999");
        final int k_width = 30; //g2.getFontMetrics(table_font).stringWidth("999");

        Graphics2D t1 = (Graphics2D) g2.create();
        t1.translate(0, header_height + tableheader_margintop);
        Graphics2D t2 = (Graphics2D) t1.create();
        t2.translate(col_width + col_margin, 0);

        for (Graphics2D g : new Graphics2D[]{t1, t2}) {
            g.setColor(new Color(255, 255, 255, 192));
            g.fillRect(0, 0, col_width, tableheader_height); // Background
            g.setColor(Color.BLACK);
            g.setFont(tableheader_font);
            g.drawString("PLAYER", table_edgepadding, tableheader_height - tableheader_paddingbottom); // Player
            GUI.drawString_centerHoriz(g, "PING", col_width - ping_width, tableheader_height - tableheader_paddingbottom, ping_width); // Ping
            GUI.drawString_centerHoriz(g, "D", col_width - ping_width - k_width, tableheader_height - tableheader_paddingbottom, k_width); // Deaths
            GUI.drawString_centerHoriz(g, "K", col_width - ping_width - 2 * k_width, tableheader_height - tableheader_paddingbottom, k_width); // Kills
        }

        // TABLE ROWS
        final int row_height = 30;
        final int head_height = 14;
        final int row_marginbottom = 2;
        final int row_paddingbottom = 10;
        final Font row_font = GameClient.FONT_SEMIBOLD.deriveFont(14f);

        Graphics2D redP = (Graphics2D) t1.create();
        Graphics2D blueP = (Graphics2D) t2.create();
        for (Graphics2D g : new Graphics2D[]{redP, blueP})
            g.translate(0, tableheader_height + tableheader_marginbottom);
        for (Player player : game.players) {
            Graphics2D g;
            if (player.team == Team.RED) {
                g = redP;
                g.setColor(new Color(255, 0, 0, 192));
            } else if (player.team == Team.BLUE) {
                g = blueP;
                g.setColor(new Color(0, 0, 255, 192));
            } else
                continue;

            g.fillRect(0, 0, col_width, row_height); // Background

            Sprite head = null;
            if (player.charClass == CharClass.ROCKETMAN) {
                head = Game.getSprite("rocketman_head");
            } else if (player.charClass == CharClass.NINJA) {
                if (player.isRed())
                    head = Game.getSprite("ninja_head_red");
                else
                    head = Game.getSprite("ninja_head_blue");
            } else if (player.charClass == CharClass.COMMANDO) {
                if (player.isRed())
                    head = Game.getSprite("commando_head_red");
                else
                    head = Game.getSprite("commando_head_blue");
            } else if (player.charClass == CharClass.SCOUT) {
                head = Game.getSprite("scout_head_menu");
            }
            if (head != null)
                g.drawImage(head.image, table_edgepadding + head.offsetX, head.offsetY + (row_height - head_height) / 2, null); // Head

            g.setColor(Color.WHITE);
            g.setFont(row_font);
            g.drawString(player.clientName, table_edgepadding * 2 + head_height, row_height - row_paddingbottom); // Name
            GUI.drawString_centerHoriz(g, player.ping + "", col_width - ping_width, row_height - row_paddingbottom, ping_width - 11); // Ping
            GUI.drawString_centerHoriz(g, player.deaths + "", col_width - ping_width - k_width, row_height - row_paddingbottom, k_width); // Deaths
            GUI.drawString_centerHoriz(g, player.kills + "", col_width - ping_width - 2 * k_width, row_height - row_paddingbottom, k_width); // Kills

            Graphics2D ping = (Graphics2D) g.create();
            ping.translate(col_width - 17, row_paddingbottom);
            int bars;
            if (player.ping >= 100)
                bars = 2;
            else if (player.ping >= 75)
                bars = 3;
            else
                bars = 4;
            drawPingIcon(ping, bars); // Ping icon


            g.translate(0, row_height + row_marginbottom);
        }

    }

}
