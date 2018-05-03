import com.badlogic.gdx.Application;
import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.headless.HeadlessApplication;
import com.badlogic.gdx.graphics.GL20;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.mockito.Mockito;

/**
 * Created by BB on 21/12/2017.
 *
 * This code is required in order to get Mock Tests for LibGDX
 *
 * Without extending from this class, Tests such as SkillLibrary and UnitLibrary do not appear to work.
 * As the libgdx libraries are not loaded in
 *
 * Credits to 'http://manabreak.eu/java/2016/10/21/unittesting-libgdx.html'
 *
 */

public class GameTest {

    private static Application application;

    @BeforeClass
    public static void init() {

        application = new HeadlessApplication(new ApplicationListener() {
            @Override public void create() {}
            @Override public void resize(int width, int height) {}
            @Override public void render() {}
            @Override public void pause() {}
            @Override public void resume() {}
            @Override public void dispose() {}
        });

        Gdx.gl20 = Mockito.mock(GL20.class);
        Gdx.gl = Gdx.gl20;
    }

    @AfterClass
    public static void cleanUp() {
        application.exit();
        application = null;
    }
}

