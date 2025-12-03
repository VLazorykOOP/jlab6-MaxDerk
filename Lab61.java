import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Random;
public class Lab61 extends JPanel implements ActionListener
{
    String baseText = "Test Text";
    String currentText = baseText;
    int x = 0;
    int y = 100;
    int xStep = 2;
    int yStep = 2;
    Font[] fonts =
    {
        new Font("Serif", Font.BOLD, 24),
        new Font("SansSerif", Font.ITALIC, 28),
        new Font("Monospaced", Font.PLAIN, 22),
        new Font("Times New Roman", Font.PLAIN, 30),
        new Font("Comic Sans MS", Font.BOLD, 28),
        new Font("Papyrus", Font.PLAIN, 32),
        new Font("Wingdings", Font.PLAIN, 34),
        new Font("Aster", Font.PLAIN, 30)
    };
    Font currentFont = fonts[0];
    Random random = new Random();
    Timer timer;
    public Lab61()
    {
        timer = new Timer(10, this);
        timer.start();
    }
    protected void paintComponent(Graphics g)
    {
        super.paintComponent(g);
        g.setFont(currentFont);
        g.setColor(Color.BLUE);
        g.drawString(currentText, x, y);
    }
    public void actionPerformed(ActionEvent e)
    {
        int width = getWidth();
        int height = getHeight();
        FontMetrics metrics = getFontMetrics(currentFont);
        int textWidth = metrics.stringWidth(currentText);
        int ascent = metrics.getAscent();
        x += xStep;
        y += yStep;
        boolean hitWall = false;
        if (x < 0)
            {
                xStep = Math.abs(xStep);
                hitWall = true;
            }
        else if (x + textWidth > width)
            {
                xStep = -Math.abs(xStep);
                hitWall = true;
            }
        if (y - ascent < 0)
            {
                yStep = Math.abs(yStep);
                hitWall = true;
            }
        else if (y > height)
            {
                yStep = -Math.abs(yStep);
                hitWall = true;
            }
        if (hitWall)
            {
                Change();
            }
        repaint();
    }
    private void Change()
    {
        currentFont = fonts[random.nextInt(fonts.length)];
        StringBuilder sb = new StringBuilder();
        for (char c : baseText.toCharArray())
            {
                if (random.nextBoolean())
                    {
                        sb.append(Character.toUpperCase(c));
                    }
                else
                    {
                        sb.append(Character.toLowerCase(c));
                    }
            }
            currentText = sb.toString();
    }
    public static void main(String[] args)
    {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Lab6");
            frame.add(new Lab61());
            frame.setSize(500, 500);
            frame.setVisible(true);
        });
    }
}