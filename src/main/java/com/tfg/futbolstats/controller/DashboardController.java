//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package javafx.scene.control;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ObjectPropertyBase;
import javafx.css.CssMetaData;
import javafx.css.Styleable;
import javafx.css.StyleableIntegerProperty;
import javafx.css.StyleableObjectProperty;
import javafx.css.StyleableProperty;
import javafx.css.converter.EnumConverter;
import javafx.css.converter.SizeConverter;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.AccessibleRole;
import javafx.scene.control.skin.TextFieldSkin;

public class TextField extends TextInputControl {
    public static final int DEFAULT_PREF_COLUMN_COUNT = 12;
    private IntegerProperty prefColumnCount;
    private ObjectProperty<EventHandler<ActionEvent>> onAction;
    private ObjectProperty<Pos> alignment;

    public TextField() {
        this("");
    }

    public TextField(String var1) {
        super(new TextFieldContent());
        this.prefColumnCount = new StyleableIntegerProperty(12) {
            private int oldValue = this.get();

            protected void invalidated() {
                int var1 = this.get();
                if (var1 < 0) {
                    if (this.isBound()) {
                        this.unbind();
                    }

                    this.set(this.oldValue);
                    throw new IllegalArgumentException("value cannot be negative.");
                } else {
                    this.oldValue = var1;
                }
            }

            public CssMetaData<TextField, Number> getCssMetaData() {
                return TextField.StyleableProperties.PREF_COLUMN_COUNT;
            }

            public Object getBean() {
                return TextField.this;
            }

            public String getName() {
                return "prefColumnCount";
            }
        };
        this.onAction = new ObjectPropertyBase<EventHandler<ActionEvent>>() {
            protected void invalidated() {
                TextField.this.setEventHandler(ActionEvent.ACTION, (EventHandler)this.get());
            }

            public Object getBean() {
                return TextField.this;
            }

            public String getName() {
                return "onAction";
            }
        };
        this.getStyleClass().add("text-field");
        this.setAccessibleRole(AccessibleRole.TEXT_FIELD);
        this.setText(var1);
    }

    public CharSequence getCharacters() {
        return ((TextFieldContent)this.getContent()).characters;
    }

    public final IntegerProperty prefColumnCountProperty() {
        return this.prefColumnCount;
    }

    public final int getPrefColumnCount() {
        return this.prefColumnCount.getValue();
    }

    public final void setPrefColumnCount(int var1) {
        this.prefColumnCount.setValue(var1);
    }

    public final ObjectProperty<EventHandler<ActionEvent>> onActionProperty() {
        return this.onAction;
    }

    public final EventHandler<ActionEvent> getOnAction() {
        return (EventHandler)this.onActionProperty().get();
    }

    public final void setOnAction(EventHandler<ActionEvent> var1) {
        this.onActionProperty().set(var1);
    }

    public final ObjectProperty<Pos> alignmentProperty() {
        if (this.alignment == null) {
            this.alignment = new StyleableObjectProperty<Pos>(Pos.CENTER_LEFT) {
                public CssMetaData<TextField, Pos> getCssMetaData() {
                    return TextField.StyleableProperties.ALIGNMENT;
                }

                public Object getBean() {
                    return TextField.this;
                }

                public String getName() {
                    return "alignment";
                }
            };
        }

        return this.alignment;
    }

    public final void setAlignment(Pos var1) {
        this.alignmentProperty().set(var1);
    }

    public final Pos getAlignment() {
        return this.alignment == null ? Pos.CENTER_LEFT : (Pos)this.alignment.get();
    }

    protected Skin<?> createDefaultSkin() {
        return new TextFieldSkin(this);
    }

    String filterInput(String var1) {
        return TextInputControl.filterInput(var1, true, true);
    }

    public static List<CssMetaData<? extends Styleable, ?>> getClassCssMetaData() {
        return TextField.StyleableProperties.STYLEABLES;
    }

    public List<CssMetaData<? extends Styleable, ?>> getControlCssMetaData() {
        return getClassCssMetaData();
    }

    private static final class TextFieldContent extends TextInputControl.ContentBase {
        private StringBuilder characters = new StringBuilder();

        public String get(int var1, int var2) {
            return this.characters.substring(var1, var2);
        }

        public void insert(int var1, String var2, boolean var3) {
            var2 = TextInputControl.filterInput(var2, true, true);
            if (!var2.isEmpty()) {
                this.characters.insert(var1, var2);
                if (var3) {
                    this.fireValueChangedEvent();
                }
            }

        }

        public void delete(int var1, int var2, boolean var3) {
            if (var2 > var1) {
                this.characters.delete(var1, var2);
                if (var3) {
                    this.fireValueChangedEvent();
                }
            }

        }

        public int length() {
            return this.characters.length();
        }

        public String get() {
            return this.characters.toString();
        }

        public String getValue() {
            return this.get();
        }
    }

    private static class StyleableProperties {
        private static final CssMetaData<TextField, Pos> ALIGNMENT;
        private static final CssMetaData<TextField, Number> PREF_COLUMN_COUNT;
        private static final List<CssMetaData<? extends Styleable, ?>> STYLEABLES;

        static {
            ALIGNMENT = new CssMetaData<TextField, Pos>("-fx-alignment", new EnumConverter(Pos.class), Pos.CENTER_LEFT) {
                public boolean isSettable(TextField var1) {
                    return var1.alignment == null || !var1.alignment.isBound();
                }

                public StyleableProperty<Pos> getStyleableProperty(TextField var1) {
                    return (StyleableProperty)var1.alignmentProperty();
                }
            };
            PREF_COLUMN_COUNT = new CssMetaData<TextField, Number>("-fx-pref-column-count", SizeConverter.getInstance(), 12) {
                public boolean isSettable(TextField var1) {
                    return var1.prefColumnCount == null || !var1.prefColumnCount.isBound();
                }

                public StyleableProperty<Number> getStyleableProperty(TextField var1) {
                    return (StyleableProperty)var1.prefColumnCountProperty();
                }
            };
            ArrayList var0 = new ArrayList(TextInputControl.getClassCssMetaData());
            var0.add(ALIGNMENT);
            var0.add(PREF_COLUMN_COUNT);
            STYLEABLES = Collections.unmodifiableList(var0);
        }
    }
}
