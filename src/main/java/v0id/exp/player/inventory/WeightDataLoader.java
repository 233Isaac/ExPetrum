package v0id.exp.player.inventory;

import com.google.common.io.Files;
import com.google.gson.*;
import net.minecraftforge.oredict.OreDictionary;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.tuple.Pair;
import v0id.api.exp.data.ExPMisc;
import v0id.api.exp.inventory.IWeightProvider;
import v0id.exp.ExPetrum;

import java.io.File;
import java.io.FileOutputStream;
import java.nio.charset.StandardCharsets;

public class WeightDataLoader
{
	public static void loadAllData()
	{
		try
		{
			File dir = new File(new File(ExPetrum.configDirectory, "ExPetrum"), "CustomItemData");
			if (!dir.exists())
			{
				dir.mkdirs();
				File example = new File(dir, "example.json");
				String out = "{ \n"
						+ "  \"__comment\" : \"This is an example file of ExPetrum\'s custom weight/volume system for items. This example file will be ignored!\", \n"
						+ "  \"entries\" : \n"
						+ "  [ \n"
						+ "    {\n"
						+ "    \"__comment\" : \"This is an example entry. A file can have as many entries as you wish.\", \n"
						+ "    \"__comment_volume\" : \"This is an example of a volume. A volume is a pair of x/z coordinates that dictates the amount of slots the item takes in the inventory.\", \n"
						+ "    \"volume\" : { \"x\" : 1, \"y\" : 1 },\n"
						+ "    \"__comment_weight\" : \"This is an example of a weight. A weight is... well, weight. It is defined in gramms.\", \n"
						+ "    \"weight\" : 100, \n"
						+ "    \"__comment_id\" : \"id is the registry name of the item. Press F3+H in game to see registry names of things.\", \n"
						+ "    \"id\" : \"minecraft:apple\", \n"
						+ "    \"__comment_metadata\" : \"metadata is the damage value for the item. Useful if you need to differentiate between items with the same id. Put -1 to ignore metadata completely.\", \n"
						+ "    \"metadata\" : -1\n"
						+ "    }\n"
						+ "  ] \n"
						+ "}";
				example.createNewFile();
				FileOutputStream fos = new FileOutputStream(example);
				IOUtils.write(out, fos, StandardCharsets.UTF_8);
				IOUtils.closeQuietly(fos);
				File vanilla = new File(dir, "vanilla.json");
				vanilla.createNewFile();
				fos = new FileOutputStream(vanilla);
                IOUtils.write(IOUtils.toString(ExPetrum.class.getResourceAsStream("/assets/exp/data/vanilla.json"), StandardCharsets.UTF_8), fos, StandardCharsets.UTF_8);
			    IOUtils.closeQuietly(fos);
			}

            for (File f : dir.listFiles(f -> f.getName().endsWith(".json")))
            {
                if (f.getName().equals("example.json"))
                {
                    continue;
                }

                try
                {
                    String fileContents = Files.toString(f, StandardCharsets.UTF_8);
                    JsonParser parser = new JsonParser();
                    JsonObject data = parser.parse(fileContents).getAsJsonObject();
                    if (data.has("entries"))
                    {
                        JsonArray entries = data.get("entries").getAsJsonArray();
                        for (JsonElement e : entries)
                        {
                            if (!e.isJsonObject())
                            {
                                continue;
                            }

                            JsonObject entry = e.getAsJsonObject();
                            String id = entry.get("id").getAsString();
                            short meta = entry.get("metadata").getAsShort();
                            meta = meta == -1 ? OreDictionary.WILDCARD_VALUE : meta;
                            float weight = entry.get("weight").getAsFloat() / 1000F;
                            JsonObject volume = entry.get("volume").getAsJsonObject();
                            Pair<Byte, Byte> vol = Pair.of(volume.get("x").getAsByte(), volume.get("y").getAsByte());
                            IWeightProvider.registerVolume(id, meta, vol);
                            IWeightProvider.registerWeight(id, meta, weight);
                            ExPMisc.modLogger.info("Registered %s with meta %d with volume %s and weight %s", id, meta, vol.toString(), Float.toString(weight));
                        }
                    }
                }
                catch (JsonSyntaxException jse)
                {
                    ExPMisc.modLogger.error(f.getAbsolutePath() + " contains JSON syntax errors!", jse);
                }
                catch (JsonParseException jpe)
                {
                    ExPMisc.modLogger.error(f.getAbsolutePath() + " is not a valid JSON file!", jpe);
                }
            }
		}
		catch (Exception ex)
		{
			ExPMisc.modLogger.error("ExPetrum was unable to load custom weight/volume data from the config file!", ex);
		}
	}
}
