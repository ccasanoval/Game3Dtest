package com.cesoft.cesdoom.map;


import com.badlogic.gdx.ai.pfa.GraphPath;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.cesoft.cesdoom.managers.MazeFactory;
import com.cesoft.cesdoom.managers.WallMapFactory;
import com.cesoft.cesdoom.util.Log;
import java.util.ArrayList;

//https://libgdx.badlogicgames.com/ci/gdx-ai/docs/com/badlogic/gdx/ai/steer/behaviors/FollowPath.html
//http://tutorials.boondog.xyz/2016/12/13/pathfinding-with-jump-point-seach-2/
//https://happycoding.io/tutorials/libgdx/pathfinding
//https://www.javatips.net/api/gdx-ai-master/tests/src/com/badlogic/gdx/ai/tests/pfa/tests/InterruptibleFlatTiledAStarTest.java
//https://www.javatips.net/api/gdx-ai-master/tests/src/com/badlogic/gdx/ai/tests/pfa/tests/InterruptibleHierarchicalTiledAStarTest.java
//http://tutorials.boondog.xyz/2016/12/13/pathfinding-with-jump-point-seach-2/
//https://happycoding.io/tutorials/libgdx/pathfinding


////////////////////////////////////////////////////////////////////////////////////////////////////
//
public class Test2 {

    private static String tag = Test2.class.getSimpleName();
    private static int cx = 10;
    private static int cy = 10;
    private static int[] mapData = {
            //1 2 3 4 5 6 7 8 9
            0,1,0,0,0,0,0,0,0,0,//0
            0,0,0,0,0,0,0,0,0,0,//1
            0,1,1,1,1,1,1,1,0,0,//2
            0,1,0,1,1,0,0,1,0,0,//3
            0,1,1,0,0,0,0,1,0,0,//4
            0,0,1,0,0,0,0,0,0,0,//5
            0,0,1,1,0,0,0,1,0,0,//6
            0,0,0,1,1,1,1,1,0,0,//7
            0,0,0,0,0,0,0,0,0,0,//8
            0,0,0,0,0,1,0,0,0,0  //9
    };

    public static void main(String[] args) {
        MapGraph map = new MapGraphFactory(cx, cy).compile(mapData);

        test1(map);

        //---

        createTest();
    }


    private static void test1(MapGraph map) {
        //var path = map.findPath(map.getNode(0,0), map.getNode(cx - 1, cy - 1))
        GraphPath<Node> path = map.findPath(new Point(0,0), new Point(cx - 1, cy - 1));
        Log.INSTANCE.e(tag, "--------- 0,0 to "+(cx-1)+","+(cy-1)+" ------------N=" + path.getCount() + " ");
        for(int i=0; i < path.getCount(); i++) {
            //Log.e("TEST", "$i---------------------")
            Log.INSTANCE.e(tag, i+"---------------------" + path.get(i).getPoint());
        }

        //path = map.findPath(map.getNode(1, 1), map.getNode(2, 1))
        path = map.findPath(new Point(0,0), new Point(2, 0));
        Log.INSTANCE.e(tag, "---------------------N=" + path.getCount() + " ");
        for(int i=0; i < path.getCount(); i++) {
            Log.INSTANCE.e(tag, i+ "---------------------" + path.get(i).getPoint());
        }


        path = map.findPath(new Point(0,0), new Point(6, 6));
        Log.INSTANCE.e(tag, "---------------------N=" + path.getCount() + " ");
        for(int i=0; i < path.getCount(); i++) {
            Log.INSTANCE.e(tag, i+ "---------------------" + path.get(i).getPoint());
        }
    }


    private static void createTest() {
        float lng = MazeFactory.lng;
        float lng2 = MazeFactory.lng2;
        //float mapWidth = MazeFactory.mapWidth;
        //float mapHeight = MazeFactory.mapHeight;
        //int scale = MazeFactory.scale;
        MapGraphFactory mapFactory = MazeFactory.INSTANCE.getMapFactory();// new MapGraphFactory(mapWidth, mapHeight, scale);


        WallMapFactory wf = WallMapFactory.INSTANCE;
        //GateMapFactory wf = WallMapFactory.INSTANCE;
        int e = 0;




        /// Interior -------------
        wf.create(mapFactory, new Vector3(0*lng, 0f, +.5f*lng2), 90f, e);
        wf.create(mapFactory, new Vector3(0*lng, 0f, -.5f*lng2), 90f, e);
        //
        wf.create(mapFactory, new Vector3(+1f*lng, 0f, +2*lng), 00f, e);
        wf.create(mapFactory, new Vector3(-1f*lng, 0f, +2*lng), 00f, e);
        wf.create(mapFactory, new Vector3(+1f*lng, 0f, -2*lng), 00f, e);
        wf.create(mapFactory, new Vector3(-1f*lng, 0f, -2*lng), 00f, e);



        /// Middle -------------
        wf.create(mapFactory, new Vector3(+5*lng, 0f, +2.0f*lng2), 90f, e);
        wf.create(mapFactory, new Vector3(-5*lng, 0f, +2.0f*lng2), 90f, e);
        wf.create(mapFactory, new Vector3(+5*lng, 0f, -2.0f*lng2), 90f, e);
        wf.create(mapFactory, new Vector3(-5*lng, 0f, -2.0f*lng2), 90f, e);

        wf.create(mapFactory, new Vector3(+3*lng, 0f, +3.0f*lng2), 90f, e);
        wf.create(mapFactory, new Vector3(-3*lng, 0f, +3.0f*lng2), 90f, e);
        wf.create(mapFactory, new Vector3(+3*lng, 0f, -3.0f*lng2), 90f, e);
        wf.create(mapFactory, new Vector3(-3*lng, 0f, -3.0f*lng2), 90f, e);

        wf.create(mapFactory, new Vector3(+2*lng2, 0f, +5f*lng), 00f, e);
        wf.create(mapFactory, new Vector3(-2*lng2, 0f, +5f*lng), 00f, e);
        wf.create(mapFactory, new Vector3(+2*lng2, 0f, -5f*lng), 00f, e);
        wf.create(mapFactory, new Vector3(-2*lng2, 0f, -5f*lng), 00f, e);



        /// Exterior -------------
        //---
        wf.create(mapFactory, new Vector3(+1*lng, 0f, +4.1f*lng2), 90f, e);
        wf.create(mapFactory, new Vector3(+3*lng, 0f, +4.1f*lng2), 90f, e);
        wf.create(mapFactory, new Vector3(+5*lng, 0f, +4.1f*lng2), 90f, e);
        wf.create(mapFactory, new Vector3(+7*lng, 0f, +4.1f*lng2), 90f, e);
        //
        wf.create(mapFactory, new Vector3(-1*lng, 0f, +4.1f*lng2), 90f, e);
        wf.create(mapFactory, new Vector3(-3*lng, 0f, +4.1f*lng2), 90f, e);
        wf.create(mapFactory, new Vector3(-5*lng, 0f, +4.1f*lng2), 90f, e);
        wf.create(mapFactory, new Vector3(-7*lng, 0f, +4.1f*lng2), 90f, e);
        //
        wf.create(mapFactory, new Vector3(+1*lng, 0f, -4.1f*lng2), 90f, e);
        wf.create(mapFactory, new Vector3(+3*lng, 0f, -4.1f*lng2), 90f, e);
        wf.create(mapFactory, new Vector3(+5*lng, 0f, -4.1f*lng2), 90f, e);
        wf.create(mapFactory, new Vector3(+7*lng, 0f, -4.1f*lng2), 90f, e);
        //
        wf.create(mapFactory, new Vector3(-1*lng, 0f, -4.1f*lng2), 90f, e);
        wf.create(mapFactory, new Vector3(-3*lng, 0f, -4.1f*lng2), 90f, e);
        wf.create(mapFactory, new Vector3(-5*lng, 0f, -4.1f*lng2), 90f, e);
        wf.create(mapFactory, new Vector3(-7*lng, 0f, -4.1f*lng2), 90f, e);

        //---
        wf.create(mapFactory, new Vector3(+4*lng2, 0f, +1*lng), 00f, e);
        wf.create(mapFactory, new Vector3(+4*lng2, 0f, +3*lng), 00f, e);
        wf.create(mapFactory, new Vector3(+4*lng2, 0f, +5*lng), 00f, e);
        //
        wf.create(mapFactory, new Vector3(-4*lng2, 0f, +1*lng), 00f, e);
        wf.create(mapFactory, new Vector3(-4*lng2, 0f, +3*lng), 00f, e);
        wf.create(mapFactory, new Vector3(-4*lng2, 0f, +5*lng), 00f, e);
        wf.create(mapFactory, new Vector3(-4*lng2, 0f, +7*lng), 00f, e);
        //
        wf.create(mapFactory, new Vector3(+4*lng2, 0f, -1*lng), 00f, e);
        wf.create(mapFactory, new Vector3(+4*lng2, 0f, -3*lng), 00f, e);
        wf.create(mapFactory, new Vector3(+4*lng2, 0f, -5*lng), 00f, e);
        wf.create(mapFactory, new Vector3(+4*lng2, 0f, -7*lng), 00f, e);
        //
        wf.create(mapFactory, new Vector3(-4*lng2, 0f, -1*lng), 00f, e);
        wf.create(mapFactory, new Vector3(-4*lng2, 0f, -3*lng), 00f, e);
        wf.create(mapFactory, new Vector3(-4*lng2, 0f, -5*lng), 00f, e);
        //---
        //Exterior -------------


        // Extra Exterior ------------------
        for(int z=-11; z <= 11; z+=2) {
            wf.create(mapFactory, new Vector3(+7f * lng2, 0f, z * lng), 00f, e);
            wf.create(mapFactory, new Vector3(-7f * lng2, 0f, z * lng), 00f, e);
        }
        for(int x=-13; x <= 13; x+=2) {
            if (x == 1) {
                //GATE
                //GateFactory.create(mapFactory, Vector3(x*lng, 0f, +6f * lng2), 90f, e)
                //GateFactory.create(mapFactory, Vector3(x*lng, 0f, -6f * lng2), 90f, e)
                continue;
            }
            wf.create(mapFactory, new Vector3(x * lng, 0f, +6f * lng2), 90f, e);
            wf.create(mapFactory, new Vector3(x * lng, 0f, -6f * lng2), 90f, e);
        }
        // Extra Exterior ------------------



        mapFactory.compile();
        mapFactory.print();
        Log.INSTANCE.e(tag, "");
        Log.INSTANCE.e(tag, "");
        mapFactory.print2();

        MapGraph map = mapFactory.getMap().get(0);
        Vector2 ptIni = new Vector2(0f, -250f);
        Vector2 ptEnd = new Vector2(0f, 0f);

        //GraphPath<Node> path1 = map.findPath(map.toMapGraphCoord(ptIni), map.toMapGraphCoord(ptEnd));
        GraphPath<Node> path1 = map.findPath(map.toMapGraphCoord(ptIni), map.toMapGraphCoord(new Vector2(0f, -150f)));
        Log.INSTANCE.e(tag, "---------------------N=" + path1.getCount() + " ");
        for(int i=0; i < path1.getCount(); i++) {
            Log.INSTANCE.e(tag, i+ "---------------------" + path1.get(i).getPoint());
        }

        Log.INSTANCE.e(tag, "");

        ArrayList path2 = map.findPath(ptIni, ptEnd);
        Log.INSTANCE.e(tag, "---------------------N=" + path2.size() + " ");
        for(int i=0; i < path2.size(); i++) {
            Log.INSTANCE.e(tag, i+ "---------------------" + path2.get(i));
        }
    }
    
    

}