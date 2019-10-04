package rafradek.TF2weapons.contract;

import java.io.BufferedReader;
import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.Iterator;
import java.util.Map;

import javax.annotation.Nullable;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.common.collect.Maps;
import com.google.gson.JsonParseException;

import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementList;
import net.minecraft.advancements.AdvancementManager;
import net.minecraft.advancements.AdvancementTreeNode;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.util.JsonUtils;
import net.minecraft.util.ResourceLocation;

public class ContractManager extends AdvancementManager {
	private static final Logger LOGGER = LogManager.getLogger();
	private static final AdvancementList ADVANCEMENT_LIST = new AdvancementList();
	
	public ContractManager(File advancementsDirIn) {
		super(advancementsDirIn);
		// TODO Auto-generated constructor stub
	}

	public void reload()
    {
        ADVANCEMENT_LIST.clear();
        Map<ResourceLocation, Advancement.Builder> map = Maps.<ResourceLocation, Advancement.Builder>newHashMap();
        this.loadBuiltInAdvancements(map);
        ADVANCEMENT_LIST.loadAdvancements(map);

        for (Advancement advancement : ADVANCEMENT_LIST.getRoots())
        {
            if (advancement.getDisplay() != null)
            {
                AdvancementTreeNode.layout(advancement);
            }
        }
    }
	
	private void loadBuiltInAdvancements(Map<ResourceLocation, Advancement.Builder> map)
    {
        FileSystem filesystem = null;

        try
        {
            URL url = AdvancementManager.class.getResource("/assets/.mcassetsroot");

            if (url != null)
            {
                URI uri = url.toURI();
                Path path;

                if ("file".equals(uri.getScheme()))
                {
                    path = Paths.get(CraftingManager.class.getResource("/assets/minecraft/advancements").toURI());
                }
                else
                {
                    if (!"jar".equals(uri.getScheme()))
                    {
                        LOGGER.error("Unsupported scheme " + uri + " trying to list all built-in advancements (NYI?)");
                        return;
                    }

                    filesystem = FileSystems.newFileSystem(uri, Collections.emptyMap());
                    path = filesystem.getPath("/assets/minecraft/advancements");
                }

                Iterator<Path> iterator = Files.walk(path).iterator();

                while (iterator.hasNext())
                {
                    Path path1 = iterator.next();

                    if ("json".equals(FilenameUtils.getExtension(path1.toString())))
                    {
                        Path path2 = path.relativize(path1);
                        String s = FilenameUtils.removeExtension(path2.toString()).replaceAll("\\\\", "/");
                        ResourceLocation resourcelocation = new ResourceLocation("minecraft", s);

                        if (!map.containsKey(resourcelocation))
                        {
                            BufferedReader bufferedreader = null;

                            try
                            {
                                bufferedreader = Files.newBufferedReader(path1);
                                Advancement.Builder advancement$builder = (Advancement.Builder)JsonUtils.fromJson(GSON, bufferedreader, Advancement.Builder.class);
                                map.put(resourcelocation, advancement$builder);
                            }
                            catch (JsonParseException jsonparseexception)
                            {
                                LOGGER.error("Parsing error loading built-in advancement " + resourcelocation, (Throwable)jsonparseexception);
                            }
                            catch (IOException ioexception)
                            {
                                LOGGER.error("Couldn't read advancement " + resourcelocation + " from " + path1, (Throwable)ioexception);
                            }
                            finally
                            {
                                IOUtils.closeQuietly((Reader)bufferedreader);
                            }
                        }
                    }
                }

                return;
            }

            LOGGER.error("Couldn't find .mcassetsroot");
        }
        catch (IOException | URISyntaxException urisyntaxexception)
        {
            LOGGER.error("Couldn't get a list of all built-in advancement files", (Throwable)urisyntaxexception);
            return;
        }
        finally
        {
            IOUtils.closeQuietly((Closeable)filesystem);
        }
    }
	
	@Nullable
    public Advancement getAdvancement(ResourceLocation id)
    {
        return ADVANCEMENT_LIST.getAdvancement(id);
    }

    public Iterable<Advancement> getAdvancements()
    {
        return ADVANCEMENT_LIST.getAdvancements();
    }
}
