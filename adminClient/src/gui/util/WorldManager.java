package gui.util;

import gui.scene.management.worldNameItem.WorldNameItem;

import java.util.ArrayList;
import java.util.List;

public class WorldManager {
    private static final WorldManager instance = new WorldManager();

    private final List<WorldNameItem> worlds;

    private WorldManager() {
        worlds = new ArrayList<>();
    }

    public synchronized void addWorld(String worldName, String filePath) {
        worlds.add(new WorldNameItem(worldName, filePath));
    }

    public static WorldManager getInstance() {
        return instance;
    }

    public WorldNameItem getWorldNameItem(String worldName) {
        for (WorldNameItem worldNameItem : worlds) {
            if (worldNameItem.getName().equals(worldName)) {
                return worldNameItem;
            }
        }
        return null;
    }

    public List<WorldNameItem> getWorlds() {
        return worlds;
    }
}
