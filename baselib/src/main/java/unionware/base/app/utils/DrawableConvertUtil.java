package unionware.base.app.utils;

import java.lang.reflect.Field;

import unionware.base.R;

public class DrawableConvertUtil {

    public static int getMenuDrawable(String name) {
        if (null == name || name.isEmpty()) return R.drawable.available_skills;
        name = name.contains("-") ? name.replace("-", "_") : name;
        Field field;
        try {
            field = R.drawable.class.getField(name);
            return Integer.parseInt(field.get(null).toString());
        } catch (NoSuchFieldException | IllegalArgumentException | IllegalAccessException e) {
            e.printStackTrace();
        }
        return R.drawable.available_skills;
    }
}
