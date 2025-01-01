import org.intellij.lang.annotations.JdkConstants.FontStyle
import java.awt.*
import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent
import javax.swing.JFrame
import javax.swing.JPanel
import javax.swing.Timer

class GradientDescent : JPanel() {

    private var points : Array<Double?> = arrayOfNulls(100);
    private var mouseX : Double = 500.0
    private var ballPos : ArrayList<Double> = ArrayList<Double>();

    private var purple : Color = Color(170, 120, 166)
    private var purpleDark : Color = Color(121, 85, 118)
    private var lightBlue : Color = Color(180, 240, 211)
    private var whiteBlue : Color = Color(215, 253, 240)


    init {
        Timer(16) { // Roughly 60 FPS
            repaint()
        }.start()

        points.forEachIndexed { index, value ->
            val t : Double = index / 10.0 - 5
            points[index] = polynomial(t)
        }

        addMouseListener(object : MouseAdapter() {
            override fun mousePressed(e: MouseEvent) {
                ballPos.add(mouseX);
            }

            override fun mouseReleased(e: MouseEvent) {

            }
        })

        addMouseMotionListener(object : MouseAdapter() {
            override fun mouseMoved(e: MouseEvent) {
                mouseX = e.point.getX();
            }
        })
    }

    private fun polynomial(t : Double) : Double {
        return 0.1 * (t - 3.0) * (t + 2.0) * (t + 2.0);
    }

    private fun derivative(t : Double) : Double {
        return 0.1 * (3.0 * t * t + 3.0 * t - 8.0)
    }

    private fun tangent(tPoint : Double, tTarget : Double) : Double {
        return derivative(tPoint) * (tTarget - tPoint) + polynomial(tPoint)
    }

    override fun paintComponent(g: Graphics) {
        super.paintComponent(g)

        val canvas = g as Graphics2D

        canvas.color = purple
        canvas.fillRect(0, 0, 1000, 1000)

        canvas.color = purpleDark
        canvas.stroke = BasicStroke(3.0f)
        canvas.drawLine(500, 0, 500, 1000)
        canvas.drawLine(0, 500, 1000, 500)

        canvas.stroke = BasicStroke(1.0f)
        for (x in 0..<10) {
            canvas.drawLine(x * 100, 0, x * 100, 1000)
        }
        for (y in 0..<10) {
            canvas.drawLine(0, y * 100, 1000, y * 100)
        }

        canvas.color = whiteBlue
        g.setFont(g.getFont().deriveFont(Font.BOLD));
        canvas.drawString("Gradient Descent", 20, 20)
        canvas.drawString("x", 980, 480)
        canvas.drawString("y", 480, 20)

        canvas.color = whiteBlue
        canvas.stroke = BasicStroke(5.0f)
        var lastValue = points[0]
        points.forEachIndexed { index, value ->
            canvas.drawLine(index * 10 - 10, (-lastValue!! * 100.0 + 500).toInt(), index * 10, (-value!! * 100.0 + 500).toInt())
            lastValue = value
        }

        canvas.color = whiteBlue
        ballPos.forEachIndexed { index, value ->
            canvas.fillOval((value - 15).toInt(), (-polynomial(value / 100.0 - 5) * 100.0 + 500).toInt() - 15, 30, 30)
            ballPos[index] -= derivative(value / 100.0 - 5)
        }

        canvas.color = whiteBlue
        canvas.drawLine(
            0,
            (-tangent(mouseX / 100.0 - 5, -5.0) * 100.0 + 500).toInt(),
            1000,
            (-tangent(mouseX / 100.0 - 5, 5.0) * 100.0 + 500).toInt(),
        )

        canvas.color = whiteBlue
        canvas.fillOval(mouseX.toInt() - 15, (-polynomial(mouseX / 100.0 - 5) * 100.0 + 500).toInt() - 15, 30, 30)


    }
}

fun main() {
    val frame = JFrame("Gradient Descent")
    frame.apply {
        add(GradientDescent())
        size = Dimension(1000, 1000)
        defaultCloseOperation = JFrame.EXIT_ON_CLOSE
        isVisible = true
    }
}