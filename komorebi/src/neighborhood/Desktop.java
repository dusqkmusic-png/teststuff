package neighborhood;
import com.badlogic.gdx.backends.lwjgl3.*;
public final class Desktop {
    public static void main(String[] args) {
        Lwjgl3ApplicationConfiguration cfg=new Lwjgl3ApplicationConfiguration();
        cfg.setTitle("Komorebi • Neighborhood visual study");
        cfg.setWindowedMode(1440,900); cfg.setResizable(true);
        cfg.useVsync(true); cfg.setForegroundFPS(60); cfg.setBackBufferConfig(8,8,8,8,24,0,4);
        cfg.disableAudio(true);
        new Lwjgl3Application(new NeighborhoodGame(args),cfg);
    }
}
