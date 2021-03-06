@file:Suppress("UNUSED_PARAMETER")

package lesson6.task1

import lesson1.task1.sqr
import java.lang.Math.PI
import java.lang.Math.tan

/**
 * Точка на плоскости
 */
data class Point(val x: Double, val y: Double) {
    /**
     * Пример
     *
     * Рассчитать (по известной формуле) расстояние между двумя точками
     */
    fun distance(other: Point): Double = Math.sqrt(sqr(x - other.x) + sqr(y - other.y))
}

/**
 * Треугольник, заданный тремя точками
 */
data class Triangle(val a: Point, val b: Point, val c: Point) {
    /**
     * Пример: полупериметр
     */
    fun halfPerimeter() = (a.distance(b) + b.distance(c) + c.distance(a)) / 2.0

    /**
     * Пример: площадь
     */
    fun area(): Double {
        val p = halfPerimeter()
        return Math.sqrt(p * (p - a.distance(b)) * (p - b.distance(c)) * (p - c.distance(a)))
    }

    /**
     * Пример: треугольник содержит точку
     */
    fun contains(p: Point): Boolean {
        val abp = Triangle(a, b, p)
        val bcp = Triangle(b, c, p)
        val cap = Triangle(c, a, p)
        return abp.area() + bcp.area() + cap.area() <= area()
    }
}

/**
 * Окружность с заданным центром и радиусом
 */
data class Circle(val center: Point, val radius: Double) {
    /**
     * Простая
     *
     * Рассчитать расстояние между двумя окружностями.
     * Расстояние между непересекающимися окружностями рассчитывается как
     * расстояние между их центрами минус сумма их радиусов.
     * Расстояние между пересекающимися окружностями считать равным 0.0.
     */
    fun distance(other: Circle): Double {
        return when {
            center.distance(other.center) - radius - other.radius > 0 -> center.distance(other.center) - radius - other.radius
            else -> 0.0
        }
    }

    /**
     * Тривиальная
     *
     * Вернуть true, если и только если окружность содержит данную точку НА себе или ВНУТРИ себя
     */
    fun contains(p: Point): Boolean = p.distance(center) <= radius
}

/**
 * Отрезок между двумя точками
 */
data class Segment(val begin: Point, val end: Point)

/**
 * Средняя
 *
 * Дано множество точек. Вернуть отрезок, соединяющий две наиболее удалённые из них.
 * Если в множестве менее двух точек, бросить IllegalArgumentException
 */
fun diameter(vararg points: Point): Segment {
    if (points.size < 2) throw IllegalAccessError()
    var max = 0.0
    var x: Double
    var i = 0
    var t: Int
    var a = Segment(points[0], points[1])
    while (i <= points.size - 2) {
        t = i + 1
        while (t <= points.size - 1) {
            x = points[i].distance(points[t])
            if (x >= max) {
                max = x
                a = Segment(points[i], points[t])
            }
            t++
        }
        i++
    }
    return a
}

/**
 * Простая
 *
 * Построить окружность по её диаметру, заданному двумя точками
 * Центр её должен находиться посередине между точками, а радиус составлять половину расстояния между ними
 */
fun circleByDiameter(diameter: Segment): Circle {
    val x = (diameter.begin.x + diameter.end.x) / 2
    val y = (diameter.begin.y + diameter.end.y) / 2
    val r = diameter.begin.distance(diameter.end) / 2
    return Circle(Point(x, y), r)
}

/**
 * Прямая, заданная точкой и углом наклона (в радианах) по отношению к оси X.
 * Уравнение прямой: (y - point.y) * cos(angle) = (x - point.x) * sin(angle)
 */
data class Line(val point: Point, val angle: Double) {
    /**
     * Средняя
     *
     * Найти точку пересечения с другой линией.
     * Для этого необходимо составить и решить систему из двух уравнений (каждое для своей прямой)
     */
    fun crossPoint(other: Line): Point {

        val x1 = point.x
        val y1 = point.y

        val x2 = other.point.x
        val y2 = other.point.y
        val x: Double
        val y: Double
        if (angle == PI / 2) return Point(x1, other.angle * (x1 - x2) + y2)
        else if (other.angle == PI / 2) return Point(x2, angle * (x2 - x1) + y1)
        else {
            val a1 = tan(angle)
            val a2 = tan(other.angle)
            x = (y2 - y1 + x1 * a1 - x2 * a2) / (a1 - a2)
            y = y1 - a1 * (x1 - x)
            return Point(x, y)
        }
    }
}

/**
 * Средняя
 *
 * Построить прямую по отрезку
 */
fun lineBySegment(s: Segment): Line = Line(Point(s.begin.x, s.begin.y),
        Math.atan2((s.end.y * 1.0 - s.begin.y), (s.end.x - s.begin.x)))

/**
 * Средняя
 *
 * Построить прямую по двум точкам
 */
fun lineByPoints(a: Point, b: Point): Line = lineBySegment(Segment(a, b))

/**
 * Сложная
 *
 * Построить серединный перпендикуляр по отрезку или по двум точкам
 */
fun bisectorByPoints(a: Point, b: Point): Line {
    if (lineByPoints(a, b).angle < PI / 2) return Line(Point((a.x + b.x) / 2, (a.y + b.y) / 2), lineByPoints(a, b).angle + PI / 2)
    else return Line(Point((a.x + b.x) / 2, (a.y + b.y) / 2), lineByPoints(a, b).angle - PI / 2)
}

/**
 * Средняя
 *
 * Задан список из n окружностей на плоскости. Найти пару наименее удалённых из них.
 * Если в списке менее двух окружностей, бросить IllegalArgumentException
 */
fun findNearestCirclePair(vararg circles: Circle): Pair<Circle, Circle> = TODO()

/**
 * Очень сложная
 *
 * Дано три различные точки. Построить окружность, проходящую через них
 * (все три точки должны лежать НА, а не ВНУТРИ, окружности).
 * Описание алгоритмов см. в Интернете
 * (построить окружность по трём точкам, или
 * построить окружность, описанную вокруг треугольника - эквивалентная задача).
 */
fun circleByThreePoints(a: Point, b: Point, c: Point): Circle = Circle(bisectorByPoints(a, b).crossPoint(bisectorByPoints(b, c))
        , bisectorByPoints(a, b).crossPoint(bisectorByPoints(b, c)).distance(a))


/**
 * Очень сложная
 *
 * Дано множество точек на плоскости. Найти круг минимального радиуса,
 * содержащий все эти точки. Если множество пустое, бросить IllegalArgumentException.
 * Если множество содержит одну точку, вернуть круг нулевого радиуса с центром в данной точке.
 *
 * Примечание: в зависимости от ситуации, такая окружность может либо проходить через какие-либо
 * три точки данного множества, либо иметь своим диаметром отрезок,
 * соединяющий две самые удалённые точки в данном множестве.
 */
fun minContainingCircle(vararg points: Point): Circle = TODO()

