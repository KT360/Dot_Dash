package com.dot.dash;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.viewport.ExtendViewport;



import Objects.GameVariables;
import Screens.GameScreen;
import Screens.MenuScreen;

public class DotDash extends Game {

	SpriteBatch batch;
	OrthographicCamera camera;
	ExtendViewport viewport;
	@Override
	public void create () {
		batch = new SpriteBatch();
		camera = new OrthographicCamera(Gdx.graphics.getWidth(),Gdx.graphics.getHeight());
		viewport = new ExtendViewport(camera.viewportWidth,camera.viewportHeight);
		setScreen(new MenuScreen(batch,viewport,this));
	}

	@Override
	public void render () {
		super.render();
	}
	
	@Override
	public void dispose () {
		Gdx.app.exit();
	}
}
