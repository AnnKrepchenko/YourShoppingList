package com.krepchenko.yourshoppinglist.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.krepchenko.yourshoppinglist.R;
import com.krepchenko.yourshoppinglist.db.GoodsEntity;

public class DbHelper extends SQLiteOpenHelper {

	private static final String NAME = "goods_db";
	private static final int VERSION = 2;
	private Context context;

	public DbHelper(Context context) {
		super(context, NAME, null, VERSION);
		this.context = context;
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(GoodsEntity.CREATE_SCRIPT);
		db.execSQL("INSERT INTO all_goods VALUES(0,'"+ context.getString(R.string.bread)+ "','GENERAL',0,0,0)");
		db.execSQL("INSERT INTO all_goods VALUES(1,'"+ context.getString(R.string.pepper)+ "','GENERAL',0,0,0)");
		db.execSQL("INSERT INTO all_goods VALUES(2,'"+ context.getString(R.string.salt)+ "','GENERAL',0,0,0)");
		db.execSQL("INSERT INTO all_goods VALUES(3,'"+ context.getString(R.string.soy_sause)+ "','GENERAL',0,0,0)");
		db.execSQL("INSERT INTO all_goods VALUES(4,'"+ context.getString(R.string.tomato_paste)+ "','GENERAL',0,0,0)");
		db.execSQL("INSERT INTO all_goods VALUES(5,'"+ context.getString(R.string.stew)+ "','GENERAL',0,0,0)");
		db.execSQL("INSERT INTO all_goods VALUES(6,'"+ context.getString(R.string.champignons)+ "','GENERAL',0,0,0)");
		db.execSQL("INSERT INTO all_goods VALUES(7,'"+ context.getString(R.string.beans)+ "','GENERAL',0,0,0)");
		db.execSQL("INSERT INTO all_goods VALUES(8,'"+ context.getString(R.string.canned_corn)+ "','GENERAL',0,0,0)");
		db.execSQL("INSERT INTO all_goods VALUES(9,'"+ context.getString(R.string.peas)+ "','GENERAL',0,0,0)");
		db.execSQL("INSERT INTO all_goods VALUES(10,'"+ context.getString(R.string.dumplings)+ "','GENERAL',0,0,0)");
		db.execSQL("INSERT INTO all_goods VALUES(11,'"+ context.getString(R.string.crab_sticks)+ "','GENERAL',0,0,0)");
		db.execSQL("INSERT INTO all_goods VALUES(12,'"+ context.getString(R.string.herring)+ "','GENERAL',0,0,0)");
		db.execSQL("INSERT INTO all_goods VALUES(13,'"+ context.getString(R.string.fish)+ "','GENERAL',0,0,0)");
		db.execSQL("INSERT INTO all_goods VALUES(14,'"+ context.getString(R.string.sausages)+ "','GENERAL',0,0,0)");
		db.execSQL("INSERT INTO all_goods VALUES(15,'"+ context.getString(R.string.ham)+ "','GENERAL',0,0,0)");
		db.execSQL("INSERT INTO all_goods VALUES(16,'"+ context.getString(R.string.salami)+ "','GENERAL',0,0,0)");
		db.execSQL("INSERT INTO all_goods VALUES(17,'"+ context.getString(R.string.tongue)+ "','GENERAL',0,0,0)");
		db.execSQL("INSERT INTO all_goods VALUES(18,'"+ context.getString(R.string.liver)+ "','GENERAL',0,0,0)");
		db.execSQL("INSERT INTO all_goods VALUES(19,'"+ context.getString(R.string.minced)+ "','GENERAL',0,0,0)");
		db.execSQL("INSERT INTO all_goods VALUES(20,'"+ context.getString(R.string.chicken)+ "','GENERAL',0,0,0)");
		db.execSQL("INSERT INTO all_goods VALUES(21,'"+ context.getString(R.string.loaf)+ "','GENERAL',0,0,0)");
		db.execSQL("INSERT INTO all_goods VALUES(22,'"+ context.getString(R.string.cookies)+ "','GENERAL',0,0,0)");
		db.execSQL("INSERT INTO all_goods VALUES(23,'"+ context.getString(R.string.milk)+ "','GENERAL',0,0,0)");
		db.execSQL("INSERT INTO all_goods VALUES(24,'"+ context.getString(R.string.kefir)+ "','GENERAL',0,0,0)");
		db.execSQL("INSERT INTO all_goods VALUES(25,'"+ context.getString(R.string.sour_cream)+ "','GENERAL',0,0,0)");
		db.execSQL("INSERT INTO all_goods VALUES(26,'"+ context.getString(R.string.oil)+ "','GENERAL',0,0,0)");
		db.execSQL("INSERT INTO all_goods VALUES(27,'"+ context.getString(R.string.curds)+ "','GENERAL',0,0,0)");
		db.execSQL("INSERT INTO all_goods VALUES(28,'"+ context.getString(R.string.cheese)+ "','GENERAL',0,0,0)");
		db.execSQL("INSERT INTO all_goods VALUES(29,'"+ context.getString(R.string.cream_cheese)+ "','GENERAL',0,0,0)");
		db.execSQL("INSERT INTO all_goods VALUES(30,'"+ context.getString(R.string.mayonnaise)+ "','GENERAL',0,0,0)");
		db.execSQL("INSERT INTO all_goods VALUES(31,'"+ context.getString(R.string.ketchup)+ "','GENERAL',0,0,0)");
		db.execSQL("INSERT INTO all_goods VALUES(32,'"+ context.getString(R.string.cream)+ "','GENERAL',0,0,0)");
		db.execSQL("INSERT INTO all_goods VALUES(33,'"+ context.getString(R.string.yogurt)+ "','GENERAL',0,0,0)");
		db.execSQL("INSERT INTO all_goods VALUES(34,'"+ context.getString(R.string.eggs)+ "','GENERAL',0,0,0)");
		db.execSQL("INSERT INTO all_goods VALUES(35,'"+ context.getString(R.string.pasta)+ "','GENERAL',0,0,0)");
		db.execSQL("INSERT INTO all_goods VALUES(36,'"+ context.getString(R.string.figure)+ "','GENERAL',0,0,0)");
		db.execSQL("INSERT INTO all_goods VALUES(37,'"+ context.getString(R.string.buckwheat)+ "','GENERAL',0,0,0)");
		db.execSQL("INSERT INTO all_goods VALUES(38,'"+ context.getString(R.string.millet_groats)+ "','GENERAL',0,0,0)");
		db.execSQL("INSERT INTO all_goods VALUES(39,'"+ context.getString(R.string.oatmeat)+ "','GENERAL',0,0,0)");
		db.execSQL("INSERT INTO all_goods VALUES(40,'"+ context.getString(R.string.flour)+ "','GENERAL',0,0,0)");
		db.execSQL("INSERT INTO all_goods VALUES(41,'"+ context.getString(R.string.black_tea)+ "','GENERAL',0,0,0)");
		db.execSQL("INSERT INTO all_goods VALUES(42,'"+ context.getString(R.string.green_tea)+ "','GENERAL',0,0,0)");
		db.execSQL("INSERT INTO all_goods VALUES(43,'"+ context.getString(R.string.coffee)+ "','GENERAL',0,0,0)");
		db.execSQL("INSERT INTO all_goods VALUES(44,'"+ context.getString(R.string.sugar)+ "','GENERAL',0,0,0)");
		db.execSQL("INSERT INTO all_goods VALUES(45,'"+ context.getString(R.string.fructose)+ "','GENERAL',0,0,0)");
		db.execSQL("INSERT INTO all_goods VALUES(46,'"+ context.getString(R.string.potatoes)+ "','GENERAL',0,0,0)");
		db.execSQL("INSERT INTO all_goods VALUES(47,'"+ context.getString(R.string.carrots)+ "','GENERAL',0,0,0)");
		db.execSQL("INSERT INTO all_goods VALUES(48,'"+ context.getString(R.string.beet)+ "','GENERAL',0,0,0)");
		db.execSQL("INSERT INTO all_goods VALUES(49,'"+ context.getString(R.string.onion)+ "','GENERAL',0,0,0)");
		db.execSQL("INSERT INTO all_goods VALUES(50,'"+ context.getString(R.string.garlic)+ "','GENERAL',0,0,0)");
		db.execSQL("INSERT INTO all_goods VALUES(51,'"+ context.getString(R.string.cabbage)+ "','GENERAL',0,0,0)");
		db.execSQL("INSERT INTO all_goods VALUES(52,'"+ context.getString(R.string.broccoli)+ "','GENERAL',0,0,0)");
		db.execSQL("INSERT INTO all_goods VALUES(53,'"+ context.getString(R.string.cauliflower)+ "','GENERAL',0,0,0)");
		db.execSQL("INSERT INTO all_goods VALUES(54,'"+ context.getString(R.string.dill)+ "','GENERAL',0,0,0)");
		db.execSQL("INSERT INTO all_goods VALUES(55,'"+ context.getString(R.string.parsley)+ "','GENERAL',0,0,0)");
		db.execSQL("INSERT INTO all_goods VALUES(56,'"+ context.getString(R.string.basil)+ "','GENERAL',0,0,0)");
		db.execSQL("INSERT INTO all_goods VALUES(57,'"+ context.getString(R.string.turnip)+ "','GENERAL',0,0,0)");
		db.execSQL("INSERT INTO all_goods VALUES(58,'"+ context.getString(R.string.pumpkin)+ "','GENERAL',0,0,0)");
		db.execSQL("INSERT INTO all_goods VALUES(59,'"+ context.getString(R.string.tomatoes)+ "','GENERAL',0,0,0)");
		db.execSQL("INSERT INTO all_goods VALUES(60,'"+ context.getString(R.string.cucumbers)+ "','GENERAL',0,0,0)");
		db.execSQL("INSERT INTO all_goods VALUES(61,'"+ context.getString(R.string.paprica)+ "','GENERAL',0,0,0)");
		db.execSQL("INSERT INTO all_goods VALUES(62,'"+ context.getString(R.string.apples)+ "','GENERAL',0,0,0)");
		db.execSQL("INSERT INTO all_goods VALUES(63,'"+ context.getString(R.string.oranges)+ "','GENERAL',0,0,0)");
		db.execSQL("INSERT INTO all_goods VALUES(64,'"+ context.getString(R.string.mandarines)+ "','GENERAL',0,0,0)");
		db.execSQL("INSERT INTO all_goods VALUES(65,'"+ context.getString(R.string.bananas)+ "','GENERAL',0,0,0)");
		db.execSQL("INSERT INTO all_goods VALUES(66,'"+ context.getString(R.string.pears)+ "','GENERAL',0,0,0)");
		db.execSQL("INSERT INTO all_goods VALUES(67,'"+ context.getString(R.string.grapes)+ "','GENERAL',0,0,0)");
		db.execSQL("INSERT INTO all_goods VALUES(68,'"+ context.getString(R.string.garnet)+ "','GENERAL',0,0,0)");
		db.execSQL("INSERT INTO all_goods VALUES(69,'"+ context.getString(R.string.persimmon)+ "','GENERAL',0,0,0)");
		db.execSQL("INSERT INTO all_goods VALUES(70,'"+ context.getString(R.string.plum)+ "','GENERAL',0,0,0)");
		db.execSQL("INSERT INTO all_goods VALUES(71,'"+ context.getString(R.string.peaches)+ "','GENERAL',0,0,0)");
		db.execSQL("INSERT INTO all_goods VALUES(72,'"+ context.getString(R.string.nuts)+ "','GENERAL',0,0,0)");
		db.execSQL("INSERT INTO all_goods VALUES(73,'"+ context.getString(R.string.dried_apricots)+ "','GENERAL',0,0,0)");
		db.execSQL("INSERT INTO all_goods VALUES(74,'"+ context.getString(R.string.raisins)+ "','GENERAL',0,0,0)");
		db.execSQL("INSERT INTO all_goods VALUES(75,'"+ context.getString(R.string.prunes)+ "','GENERAL',0,0,0)");
		db.execSQL("INSERT INTO all_goods VALUES(76,'"+ context.getString(R.string.juice)+ "','GENERAL',0,0,0)");
		db.execSQL("INSERT INTO all_goods VALUES(77,'"+ context.getString(R.string.pork)+ "','GENERAL',0,0,0)");
		db.execSQL("INSERT INTO all_goods VALUES(78,'"+ context.getString(R.string.beef)+ "','GENERAL',0,0,0)");
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
	}

	@Override
	public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
	}
	
	@Override
	public void onOpen(SQLiteDatabase db) {
		super.onOpen(db);
	}

}
