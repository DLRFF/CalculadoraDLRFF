package com.ferdelarosa.calculadora

import android.os.Bundle
import android.text.InputType
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.annotation.DrawableRes
import androidx.annotation.IdRes
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.ferdelarosa.calculadora.databinding.ActivityMainBinding
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import java.text.NumberFormat
import kotlin.math.floor
import kotlin.math.log10
import kotlin.math.pow
import kotlin.math.sqrt

/**
 * Modelo de datos para la configuración de cada campo de entrada.
 */
data class CampoConfig(
    @StringRes val etiquetaRes: Int,
    @StringRes val unidadRes: Int? = null,
    @StringRes val ayudaRes: Int,
    val inputType: Int
)

/**
 * Catálogo de los 4 cálculos disponibles con su chip asociado,
 * la imagen de la fórmula y sus campos de entrada.
 */
enum class TipoCalculo(
    @IdRes val chipId: Int,
    @DrawableRes val formulaRes: Int,
    val campos: List<CampoConfig>
) {
    CUADRATICA(
        chipId = R.id.chipCuadratica,
        formulaRes = R.drawable.formula_cuadratica,
        campos = listOf(
            CampoConfig(
                etiquetaRes = R.string.etiqueta_cuad_a,
                unidadRes = null,
                ayudaRes = R.string.ayuda_cuad_a,
                inputType = InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_FLAG_DECIMAL or InputType.TYPE_NUMBER_FLAG_SIGNED
            ),
            CampoConfig(
                etiquetaRes = R.string.etiqueta_cuad_b,
                unidadRes = null,
                ayudaRes = R.string.ayuda_cuad_b,
                inputType = InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_FLAG_DECIMAL or InputType.TYPE_NUMBER_FLAG_SIGNED
            ),
            CampoConfig(
                etiquetaRes = R.string.etiqueta_cuad_c,
                unidadRes = null,
                ayudaRes = R.string.ayuda_cuad_c,
                inputType = InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_FLAG_DECIMAL or InputType.TYPE_NUMBER_FLAG_SIGNED
            )
        )
    ),
    LED(
        chipId = R.id.chipLed,
        formulaRes = R.drawable.formula_led,
        campos = listOf(
            CampoConfig(
                etiquetaRes = R.string.etiqueta_led_vf,
                unidadRes = R.string.unidad_led_vf,
                ayudaRes = R.string.ayuda_led_vf,
                inputType = InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_FLAG_DECIMAL
            ),
            CampoConfig(
                etiquetaRes = R.string.etiqueta_led_vled,
                unidadRes = R.string.unidad_led_vled,
                ayudaRes = R.string.ayuda_led_vled,
                inputType = InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_FLAG_DECIMAL
            ),
            CampoConfig(
                etiquetaRes = R.string.etiqueta_led_i,
                unidadRes = R.string.unidad_led_i,
                ayudaRes = R.string.ayuda_led_i,
                inputType = InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_FLAG_DECIMAL
            )
        )
    ),
    BICI(
        chipId = R.id.chipBici,
        formulaRes = R.drawable.formula_bici,
        campos = listOf(
            CampoConfig(
                etiquetaRes = R.string.etiqueta_bici_plato,
                unidadRes = R.string.unidad_bici_plato,
                ayudaRes = R.string.ayuda_bici_plato,
                inputType = InputType.TYPE_CLASS_NUMBER
            ),
            CampoConfig(
                etiquetaRes = R.string.etiqueta_bici_pinon,
                unidadRes = R.string.unidad_bici_pinon,
                ayudaRes = R.string.ayuda_bici_pinon,
                inputType = InputType.TYPE_CLASS_NUMBER
            ),
            CampoConfig(
                etiquetaRes = R.string.etiqueta_bici_circ,
                unidadRes = R.string.unidad_bici_circ,
                ayudaRes = R.string.ayuda_bici_circ,
                inputType = InputType.TYPE_CLASS_NUMBER
            ),
            CampoConfig(
                etiquetaRes = R.string.etiqueta_bici_cadencia,
                unidadRes = R.string.unidad_bici_cadencia,
                ayudaRes = R.string.ayuda_bici_cadencia,
                inputType = InputType.TYPE_CLASS_NUMBER
            )
        )
    ),
    INTERES(
        chipId = R.id.chipInteres,
        formulaRes = R.drawable.formula_interes,
        campos = listOf(
            CampoConfig(
                etiquetaRes = R.string.etiqueta_int_capital,
                unidadRes = R.string.unidad_int_capital,
                ayudaRes = R.string.ayuda_int_capital,
                inputType = InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_FLAG_DECIMAL
            ),
            CampoConfig(
                etiquetaRes = R.string.etiqueta_int_tasa,
                unidadRes = R.string.unidad_int_tasa,
                ayudaRes = R.string.ayuda_int_tasa,
                inputType = InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_FLAG_DECIMAL
            ),
            CampoConfig(
                etiquetaRes = R.string.etiqueta_int_n,
                unidadRes = R.string.unidad_int_n,
                ayudaRes = R.string.ayuda_int_n,
                inputType = InputType.TYPE_CLASS_NUMBER
            ),
            CampoConfig(
                etiquetaRes = R.string.etiqueta_int_tiempo,
                unidadRes = R.string.unidad_int_tiempo,
                ayudaRes = R.string.ayuda_int_tiempo,
                inputType = InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_FLAG_DECIMAL
            )
        )
    )
}

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var slots: List<Pair<TextInputLayout, TextInputEditText>>
    private var calculoActual: TipoCalculo = TipoCalculo.CUADRATICA

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // 1. Inflar la vista con ViewBinding
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 2. Configurar padding de barras del sistema
        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // 3. Mapear los 4 slots de vistas (Layout + EditText)
        slots = listOf(
            binding.tilCampo1 to binding.etCampo1,
            binding.tilCampo2 to binding.etCampo2,
            binding.tilCampo3 to binding.etCampo3,
            binding.tilCampo4 to binding.etCampo4
        )

        // 4. Listener de cambio de selección en el ChipGroup
        binding.cgCalculos.setOnCheckedStateChangeListener { _, checkedIds ->
            val selectedId = checkedIds.firstOrNull() ?: return@setOnCheckedStateChangeListener
            val calculoSeleccionado = TipoCalculo.entries.firstOrNull { it.chipId == selectedId } ?: return@setOnCheckedStateChangeListener
            aplicarCalculo(calculoSeleccionado)
        }

        // 5. Botón Calcular
        binding.btnCalcular.setOnClickListener {
            validarYCalcular()
        }

        // 6. Restaurar estado tras rotación de pantalla o iniciar por defecto
        if (savedInstanceState != null) {
            val chipId = savedInstanceState.getInt("KEY_CALCULO_ID", R.id.chipCuadratica)
            val calculoGuardado = TipoCalculo.entries.firstOrNull { it.chipId == chipId } ?: TipoCalculo.CUADRATICA
            aplicarCalculo(calculoGuardado, limpiarCampos = false)
            val resultado = savedInstanceState.getString("KEY_RESULTADO")
            val visibilidad = savedInstanceState.getInt("KEY_RESULTADO_VISIBILITY", View.GONE)
            if (resultado != null && visibilidad == View.VISIBLE) {
                binding.tvResultado.text = resultado
                binding.cardResultado.visibility = View.VISIBLE
            }
        } else {
            aplicarCalculo(TipoCalculo.CUADRATICA)
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt("KEY_CALCULO_ID", calculoActual.chipId)
        outState.putString("KEY_RESULTADO", binding.tvResultado.text?.toString())
        outState.putInt("KEY_RESULTADO_VISIBILITY", binding.cardResultado.visibility)
    }

    /**
     * Reconfigura dinámicamente los campos según el cálculo elegido:
     * - Oculta la tarjeta de resultado (si no es rotación)
     * - Cambia la imagen de la fórmula
     * - Muestra únicamente los campos requeridos
     * - Asigna etiquetas (hint), unidades (suffixText), ayudas (helperText) y teclado (inputType)
     * - Manda a GONE los campos sobrantes
     */
    private fun aplicarCalculo(calculo: TipoCalculo, limpiarCampos: Boolean = true) {
        calculoActual = calculo

        if (limpiarCampos) {
            // Ocultar tarjeta de resultado previo
            binding.cardResultado.visibility = View.GONE
        }

        // Actualizar imagen de la fórmula
        binding.ivFormula.setImageResource(calculo.formulaRes)

        // Configurar cada uno de los 4 slots
        for (i in slots.indices) {
            val (til, et) = slots[i]

            // Limpiar valores y errores previos solo si el usuario cambió de cálculo manualmente
            if (limpiarCampos) {
                et.text?.clear()
                til.error = null
            }

            if (i < calculo.campos.size) {
                val config = calculo.campos[i]
                til.visibility = View.VISIBLE
                til.hint = getString(config.etiquetaRes)
                til.suffixText = config.unidadRes?.let { getString(it) }
                til.helperText = getString(config.ayudaRes)
                et.inputType = config.inputType
            } else {
                til.visibility = View.GONE
            }
        }
    }

    /**
     * Nivel 1 de validación:
     * Lee el texto del EditText, verifica que no esté vacío y que sea numérico válido.
     */
    private fun leerNumero(til: TextInputLayout, et: TextInputEditText): Double? {
        val texto = et.text?.toString()?.trim()
        if (texto.isNullOrEmpty()) {
            til.error = getString(R.string.err_campo_obligatorio)
            return null
        }
        val numero = texto.toDoubleOrNull()
        if (numero == null) {
            til.error = getString(R.string.err_numero_invalido)
            return null
        }
        til.error = null
        return numero
    }

    /**
     * Valida los campos según el cálculo seleccionado (Niveles 1, 2 y 3).
     * Si pasa todos los filtros, procesa el cálculo y muestra el resultado.
     */
    private fun validarYCalcular() {
        ocultarTeclado()
        when (calculoActual) {
            TipoCalculo.CUADRATICA -> calcularCuadratica()
            TipoCalculo.LED -> calcularLed()
            TipoCalculo.BICI -> calcularBici()
            TipoCalculo.INTERES -> calcularInteres()
        }
    }

    private fun ocultarTeclado() {
        val imm = getSystemService(INPUT_METHOD_SERVICE) as? android.view.inputmethod.InputMethodManager
        currentFocus?.let { view ->
            imm?.hideSoftInputFromWindow(view.windowToken, 0)
        }
    }

    private fun calcularCuadratica() {
        val a = leerNumero(binding.tilCampo1, binding.etCampo1)
        val b = leerNumero(binding.tilCampo2, binding.etCampo2)
        val c = leerNumero(binding.tilCampo3, binding.etCampo3)

        var valido = (a != null && b != null && c != null)

        // Nivel 2: 'a' no puede ser 0
        if (a != null && a == 0.0) {
            binding.tilCampo1.error = getString(R.string.err_cuad_a_cero)
            valido = false
        }

        if (!valido) return

        // Nivel 3: Relación entre campos (discriminante >= 0)
        val disc = (b!! * b) - (4.0 * a!! * c!!)
        if (disc < 0) {
            binding.tilCampo1.error = getString(R.string.err_cuad_discriminante)
            binding.cardResultado.visibility = View.GONE
            return
        }

        // Procesamiento
        if (disc == 0.0) {
            val x = -b / (2.0 * a)
            binding.tvResultado.text = getString(R.string.res_cuad_raiz_doble, x)
        } else {
            val raizDisc = sqrt(disc)
            val x1 = (-b + raizDisc) / (2.0 * a)
            val x2 = (-b - raizDisc) / (2.0 * a)
            binding.tvResultado.text = getString(R.string.res_cuad_dos_raices, x1, x2)
        }

        mostrarResultado()
    }

    private fun calcularLed() {
        val vf = leerNumero(binding.tilCampo1, binding.etCampo1)
        val vled = leerNumero(binding.tilCampo2, binding.etCampo2)
        val iMa = leerNumero(binding.tilCampo3, binding.etCampo3)

        var valido = (vf != null && vled != null && iMa != null)

        // Nivel 2: Rangos
        if (vf != null && (vf <= 0.0 || vf > 48.0)) {
            binding.tilCampo1.error = getString(R.string.err_led_vf_rango)
            valido = false
        }
        if (vled != null && (vled < 1.0 || vled > 4.0)) {
            binding.tilCampo2.error = getString(R.string.err_led_vled_rango)
            valido = false
        }
        if (iMa != null && (iMa < 1.0 || iMa > 50.0)) {
            binding.tilCampo3.error = getString(R.string.err_led_i_rango)
            valido = false
        }

        if (!valido) return

        // Nivel 3: Relación entre campos (Vf > Vled)
        if (vf!! <= vled!!) {
            binding.tilCampo1.error = getString(R.string.err_led_vf_menor)
            binding.cardResultado.visibility = View.GONE
            return
        }

        // Procesamiento
        val iAmp = iMa!! / 1000.0
        val rCalculada = (vf - vled) / iAmp
        val e12Comercial = obtenerComercialE12(rCalculada)
        val potenciaMw = (vf - vled) * iMa

        binding.tvResultado.text = getString(R.string.res_led, rCalculada, e12Comercial, potenciaMw)
        mostrarResultado()
    }

    private fun calcularBici() {
        val plato = leerNumero(binding.tilCampo1, binding.etCampo1)
        val pinon = leerNumero(binding.tilCampo2, binding.etCampo2)
        val circ = leerNumero(binding.tilCampo3, binding.etCampo3)
        val cadencia = leerNumero(binding.tilCampo4, binding.etCampo4)

        var valido = (plato != null && pinon != null && circ != null && cadencia != null)

        // Nivel 2: Rangos de enteros
        if (plato != null && (plato < 22 || plato > 60)) {
            binding.tilCampo1.error = getString(R.string.err_bici_plato_rango)
            valido = false
        }
        if (pinon != null && (pinon < 9 || pinon > 36)) {
            binding.tilCampo2.error = getString(R.string.err_bici_pinon_rango)
            valido = false
        }
        if (circ != null && (circ < 1000 || circ > 2400)) {
            binding.tilCampo3.error = getString(R.string.err_bici_circ_rango)
            valido = false
        }
        if (cadencia != null && (cadencia < 10 || cadencia > 150)) {
            binding.tilCampo4.error = getString(R.string.err_bici_cadencia_rango)
            valido = false
        }

        if (!valido) return

        // Procesamiento
        val relacion = plato!! / pinon!!
        val desarrolloM = relacion * (circ!! / 1000.0)
        val velocidadKmh = desarrolloM * cadencia!! * 60.0 / 1000.0

        binding.tvResultado.text = getString(R.string.res_bici, relacion, desarrolloM, velocidadKmh)
        mostrarResultado()
    }

    private fun calcularInteres() {
        val capital = leerNumero(binding.tilCampo1, binding.etCampo1)
        val tasa = leerNumero(binding.tilCampo2, binding.etCampo2)
        val n = leerNumero(binding.tilCampo3, binding.etCampo3)
        val tiempo = leerNumero(binding.tilCampo4, binding.etCampo4)

        var valido = (capital != null && tasa != null && n != null && tiempo != null)

        // Nivel 2: Rangos
        if (capital != null && capital <= 0.0) {
            binding.tilCampo1.error = getString(R.string.err_int_capital_rango)
            valido = false
        }
        if (tasa != null && (tasa < 0.1 || tasa > 100.0)) {
            binding.tilCampo2.error = getString(R.string.err_int_tasa_rango)
            valido = false
        }
        if (n != null && (n < 1 || n > 365)) {
            binding.tilCampo3.error = getString(R.string.err_int_n_rango)
            valido = false
        }
        if (tiempo != null && (tiempo <= 0.0 || tiempo > 100.0)) {
            binding.tilCampo4.error = getString(R.string.err_int_tiempo_rango)
            valido = false
        }

        if (!valido) return

        // Procesamiento: A = P * (1 + (r/100)/n)^(n*t)
        val tasaDecimalPorPeriodo = (tasa!! / 100.0) / n!!
        val periodosTotales = n * tiempo!!
        val montoFinal = capital!! * (1.0 + tasaDecimalPorPeriodo).pow(periodosTotales)
        val interesGanado = montoFinal - capital

        val formatoMoneda = NumberFormat.getCurrencyInstance()
        val montoStr = formatoMoneda.format(montoFinal)
        val interesStr = formatoMoneda.format(interesGanado)

        binding.tvResultado.text = getString(R.string.res_interes, montoStr, interesStr)
        mostrarResultado()
    }

    /**
     * Muestra la tarjeta de resultado y realiza un scroll suave hacia abajo.
     */
    private fun mostrarResultado() {
        binding.cardResultado.visibility = View.VISIBLE
        binding.main.post {
            binding.main.smoothScrollTo(0, binding.cardResultado.bottom)
        }
    }

    /**
     * Calcula el valor comercial estándar más cercano hacia arriba en la serie E12.
     * Serie base: 1.0, 1.2, 1.5, 1.8, 2.2, 2.7, 3.3, 3.9, 4.7, 5.6, 6.8, 8.2
     */
    private fun obtenerComercialE12(resistencia: Double): String {
        if (resistencia <= 0.0) return "0 Ω"

        val e12Base = doubleArrayOf(1.0, 1.2, 1.5, 1.8, 2.2, 2.7, 3.3, 3.9, 4.7, 5.6, 6.8, 8.2)
        val exponente = floor(log10(resistencia))
        val decada = 10.0.pow(exponente)
        val normalizado = resistencia / decada

        val valorE12 = e12Base.firstOrNull { it >= normalizado }
        val rComercial = if (valorE12 != null) {
            valorE12 * decada
        } else {
            1.0 * 10.0.pow(exponente + 1)
        }

        return if (rComercial >= 1000.0) {
            String.format(java.util.Locale.getDefault(), "%.1f kΩ", rComercial / 1000.0)
        } else {
            String.format(java.util.Locale.getDefault(), "%.0f Ω", rComercial)
        }
    }
}