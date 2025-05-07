package unionware.base.model.bean.barcode

/**
 * 带有校准值 的条码
 */
class CalibrateBarCodeBean(
    value: String
) : BarCodeBean(value) {
    /**
     * 4mA校准值
     */
    var calibration4: String? = null
    /**
     * 4mA校准值文本
     */
    var calibration4Text: String? = null

    /**
     * 20mA校准值
     */
    var calibration20: String? = null

    /**
     * 20mA校准值文本
     */
    var calibration20Text: String? = null
}