package com.cesoft.cesdoom.map;


import com.badlogic.gdx.ai.pfa.GraphPath;
import com.cesoft.cesdoom.util.Log;

public class Test2
{
    private static int cx = 10;
    private static int cy = 10;
    private static int[] mapData = {
            // 1  2  3  4  5  6  7  8  9
            0, 1, 0, 0, 0, 0, 0, 0, 0, 0, //0
            0, 0, 0, 0, 0, 0, 0, 0, 0, 0, //1
            0, 0, 0, 1, 0, 0, 0, 1, 0, 0, //2
            0, 0, 0, 1, 1, 0, 1, 1, 0, 0, //3
            0, 0, 0, 0, 0, 0, 0, 0, 0, 0, //4
            0, 0, 0, 0, 0, 0, 0, 0, 0, 0, //5
            0, 0, 0, 1, 1, 1, 1, 0, 0, 0, //6
            0, 0, 0, 0, 0, 0, 1, 0, 0, 0, //7
            0, 0, 0, 0, 0, 0, 0, 0, 0, 0, //8
            0, 0, 0, 0, 0, 1, 0, 0, 0, 0  //9
    };

    public static void main(String[] args) {
        MapGraph map = new MapGraphFactory(cx, cy).compile(mapData);

        //var path = map.findPath(map.getNode(0, 0), map.getNode(cx - 1, cy - 1))
        GraphPath<Node> path = map.findPath(new Point(0, 0), new Point(cx - 1, cy - 1));
        Log.INSTANCE.e("TEST", "\n--------- 0,0 to ${cx-1},${cy-1} ------------N=" + path.getCount() + " ");
        for(int i=0; i < path.getCount(); i++) {
            //Log.e("TEST", "$i---------------------")
            Log.INSTANCE.e("TEST", i+"---------------------" + path.get(i).getPoint());
        }

        //path = map.findPath(map.getNode(1, 1), map.getNode(2, 1))
        path = map.findPath(new Point(0, 0), new Point(2, 0));
        Log.INSTANCE.e("TEST", "\n---------------------N=" + path.getCount() + " ");
        for(int i=0; i < path.getCount(); i++) {
            Log.INSTANCE.e("TEST", i+ "---------------------" + path.get(i).getPoint());
        }
    }

}