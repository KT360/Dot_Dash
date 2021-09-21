package Objects;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Cell;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Array;

import java.util.ArrayList;


public class GuiElement extends Table{

    Button[] buttons;

    //Creates a Table
    //The {fieldNumb} parameter decides how many buttons should be added
    public GuiElement(int fieldNumb,float width,float height){

        Pixmap pix = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pix.setColor(0xDEADBEFF); // DE is red, AD is green and BE is blue.
        pix.fill();
        Texture defaultSkin = new Texture(pix);

        setDebug(true);
        setWidth(width);
        setHeight(height);

        buttons = new Button[fieldNumb];

        //Initialize butons/Text
        for (int button = 0; button<buttons.length; button++)
        {
            buttons[button] = new Button(new TextureRegionDrawable(defaultSkin));
            add(buttons[button]).width(100).height(100);
            row();
        }

        //Not using it anymore
        pix.dispose();
        defaultSkin.dispose();
    }

    public GuiElement(int fieldNumb){

        Pixmap pix = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pix.setColor(0xDEADBEFF); // DE is red, AD is green and BE is blue.
        pix.fill();
        Texture defaultSkin = new Texture(pix);

        setDebug(true);

        buttons = new Button[fieldNumb];

        //Initialize butons/Text
        for (int button = 0; button<buttons.length; button++)
        {
            buttons[button] = new Button(new TextureRegionDrawable(defaultSkin));
            add(buttons[button]).width(100).height(100);
            row();
        }

        //Not using it anymore
        pix.dispose();
        defaultSkin.dispose();
    }

    //Set style of button at designated index
    public void setButtonStyle(int index, TextureRegion upRegion, TextureRegion downRegion){

        Button.ButtonStyle style = new Button.ButtonStyle();
        style.up = new TextureRegionDrawable(upRegion);
        style.down = new TextureRegionDrawable(downRegion);
        buttons[index].setStyle(style);

    }

    //Get the designated cell inside the table
    public Cell getField(int index){
        return getCells().get(index);
    }


    @Override
    public void draw(Batch batch, float parentAlpha) {
        super.draw(batch,parentAlpha);
    }


}
