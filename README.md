# CalculadoraDLRFF — Calculadora Interactiva con Formularios Dinámicos

![Android](https://img.shields.io/badge/Android-SDK%2028%2B-3DDC84?style=for-the-badge&logo=android&logoColor=white)
![Kotlin](https://img.shields.io/badge/Kotlin-2.0-7F52FF?style=for-the-badge&logo=kotlin&logoColor=white)
![Material Design 3](https://img.shields.io/badge/Material_Design-3-6750A4?style=for-the-badge&logo=materialdesign&logoColor=white)
![UNAM FI](https://img.shields.io/badge/UNAM-Facultad_de_Ingenier%C3%ADa-002B49?style=for-the-badge)

Aplicación móvil nativa para Android desarrollada en **Kotlin** con **XML Views**, enfocada en la resolución interactiva de problemas matemáticos, electrónicos, mecánicos y financieros mediante **formularios dinámicos, validación estricta y arquitectura limpia**.

> **Materia:** Cómputo Móvil  
> **Institución:** Facultad de Ingeniería, Universidad Nacional Autónoma de México (UNAM)  
> **Autor:** Fernando De la Rosa Flores  

---

## 📱 Descripción del Proyecto

La aplicación implementa una interfaz con **Material Design 3** donde el formulario se reconfigura dinámicamente en tiempo de ejecución según el cálculo seleccionado. 

---

## ✨ Características Principales

* 🔄 **Formularios Dinámicos y Reactivos:** Menú de selección mediante `ChipGroup` de selección exclusiva. La interfaz ajusta etiquetas (`hint`), unidades de medida (`suffixText`), ayudas (`helperText`), tipo de teclado (`inputType`) e ilustración de fórmula. Los campos innecesarios se ocultan completamente con `View.GONE` sin dejar huecos en blanco.
* 🛡️ **Validación Robusta en 3 Niveles:**
  1. **Nivel 1 (Entrada):** Detección de campos obligatorios vacíos y entradas numéricas inválidas (empleando `toDoubleOrNull()` para evitar excepciones `NumberFormatException`).
  2. **Nivel 2 (Rangos y Dominio):** Restricción de $a \neq 0$ en cuadrática, rangos de voltaje/corriente en LED, dientes y cadencia en ciclismo, y tasas/plazos en finanzas.
  3. **Nivel 3 (Relaciones entre campos):**
     * **LED:** Comprueba que el voltaje de la fuente sea estrictamente mayor a la caída de tensión del LED ($V_f > V_{led}$).
     * **Cuadrática:** Evalúa el discriminante ($b^2 - 4ac \ge 0$) alertando en el campo cuando no existen raíces reales.
* ⚡ **Procesamiento de Ingeniería:**
  * **Serie E12 Internacional:** Algoritmo logarítmico para aproximar la resistencia calculada al valor comercial estándar hacia arriba (1.0, 1.2, 1.5, 1.8, 2.2, 2.7, 3.3, 3.9, 4.7, 5.6, 6.8, 8.2 $\times 10^n$).
  * **Formato Monetario Internacional:** Empleo de `NumberFormat.getCurrencyInstance()` para adaptar el formato de moneda a la región del dispositivo.
* 🌐 **Soporte Bilingüe Nativo (i18n):** 100% de los textos (etiquetas, ayudas, errores, unidades y resultados) centralizados en recursos XML:
  * Español (por defecto): `res/values/strings.xml`
  * Inglés: `res/values-en/strings.xml`
  * Cero cadenas *hard-codeadas* en código Kotlin o layouts.
* 🎨 **Gráficos Vectoriales y UI Profesional:**
  * Ilustraciones de fórmulas creadas como **Android Vector Drawables** (`res/drawable/formula_*.xml`), garantizando nitidez infinita sin pixelado en cualquier densidad de pantalla.
  * Ícono adaptativo personalizado (`Adaptive Icon`) en `res/mipmap/` y `res/drawable/` con diseño de calculadora y paleta Material 3.
  * Ocultamiento automático del teclado virtual al calcular y desplazamiento suave (`smoothScroll`) hacia la tarjeta de resultados.
  * Manejo del ciclo de vida y rotación de pantalla (`onSaveInstanceState`) para no perder datos ni resultados al cambiar la orientación del dispositivo.

---

## 📐 Los 4 Cálculos Implementados

| Cálculo | Fórmula | Entradas | Validaciones Clave | Resultado |
|---|---|:---:|---|---|
| **1. Ecuación Cuadrática** | $ax^2 + bx + c = 0$ | 3 | • $a \neq 0$<br>• $b^2 - 4ac \ge 0$ (raíces reales) | Raíces $x_1, x_2$ o raíz doble |
| **2. Resistencia LED** | $R = \frac{V_f - V_{led}}{I}$ | 3 | • $0 < V_f \le 48\text{ V}$<br>• $1.0 \le V_{led} \le 4.0\text{ V}$<br>• $1 \le I \le 50\text{ mA}$<br>• **Relación:** $V_f > V_{led}$ | Resistencia teórica ($\Omega$), valor comercial E12 ($\Omega$/$\text{k}\Omega$) y potencia disipada ($\text{mW}$) |
| **3. Desarrollo en Bici** | $\text{Des} = \frac{\text{Plato}}{\text{Piñón}} \times \text{Circ}$ | 4 | • Plato: $22\text{ a }60\text{ d}$<br>• Piñón: $9\text{ a }36\text{ d}$<br>• Circunferencia: $1000\text{ a }2400\text{ mm}$<br>• Cadencia: $10\text{ a }150\text{ rpm}$ | Relación de transmisión, desarrollo por pedalada ($\text{m}$) y velocidad estimada ($\text{km/h}$) |
| **4. Interés Compuesto** | $A = P\left(1 + \frac{r}{n}\right)^{n \cdot t}$ | 4 | • Capital $P > 0$<br>• Tasa $0.1\% \le r \le 100\%$<br>• Capitalizaciones $1 \le n \le 365$<br>• Tiempo $0 < t \le 100\text{ años}$ | Monto total acumulado y total de intereses generados (formato moneda) |

---

## 🛠️ Tecnologías y Arquitectura

* **Lenguaje:** Kotlin 2.0+
* **Framework UI:** Android XML Views con componentes Material 3 (`com.google.android.material`)
* **Acceso a Vistas:** ViewBinding (`ActivityMainBinding`)
* **Configuración Gradle:** Gradle con Kotlin DSL (`build.gradle.kts`)
* **API Mínima (`minSdk`):** API 28 (Android 9.0 Pie)
* **API Objetivo (`compileSdk` / `targetSdk`):** API 37
* **Patrón de diseño:** Desacoplamiento de catálogo mediante `data class CampoConfig` y `enum class TipoCalculo`.

---

## 🚀 Cómo Ejecutar el Proyecto

### Opción 1: Clonar desde Android Studio
1. Abre Android Studio.
2. Selecciona **File $\rightarrow$ New $\rightarrow$ Project from Version Control**.
3. Pega la URL del repositorio:
   ```bash
   https://github.com/DLRFF/CalculadoraDLRFF.git
   ```
4. Haz clic en **Clone**.
5. Espera a que termine la sincronización de Gradle y presiona **Run 'app'** (▶️) en tu emulador o dispositivo físico.

### Opción 2: Clonar por consola
```bash
git clone https://github.com/DLRFF/CalculadoraDLRFF.git
cd CalculadoraDLRFF
```
Abre la carpeta resultante en Android Studio y ejecuta la app.

---

## 👤 Autor

* **Fernando De la Rosa Flores**  
* Estudiante de Ingeniería en Computación  
* Facultad de Ingeniería — UNAM  
* GitHub: [@DLRFF](https://github.com/DLRFF)
