package ch.elmootan.core.sharedObjects;

import ch.elmootan.core.physics.Body;
import com.fasterxml.jackson.core.*;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.node.TextNode;

import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;

public class CustomObjectMapper extends ObjectMapper {

   public CustomObjectMapper()
   {
      super();
      SimpleModule module = new SimpleModule();
      module.addSerializer(Color.class, new ColorSerializer());
      module.addDeserializer(Color.class, new ColorDeserializer());
      registerModule(module);
   }

   public static class ColorSerializer extends JsonSerializer<Color> {
      @Override
      public void serialize(Color value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
         gen.writeStartObject();
         gen.writeFieldName("argb");
         gen.writeString(Integer.toHexString(value.getRGB()));
         gen.writeEndObject();
      }
   }

   public static class ColorDeserializer extends JsonDeserializer<Color> {
      @Override
      public Color deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
         TreeNode root = p.getCodec().readTree(p);
         TextNode rgba = (TextNode) root.get("argb");
         return new Color(Integer.parseUnsignedInt(rgba.textValue(), 16), true);
      }
   }

//   public static class BodiesSerializer extends JsonSerializer<List<Body>> {
//      @Override
//      public void serialize(Color value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
//         gen.writeStartObject();
//         gen.writeFieldName("argb");
//         gen.writeString(Integer.toHexString(value.getRGB()));
//         gen.writeEndObject();
//      }
//   }

//   public static class CBodiesDeserializer extends JsonDeserializer<ArrayList<Body>> {
//      @Override
//      public ArrayList<Body> deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
//         if()
//         TreeNode root = p.getCodec().readTree(p);
//         TextNode rgba = (TextNode) root.get("argb");
//         return new Color(Integer.parseUnsignedInt(rgba.textValue(), 16), true);
//      }
//   }


}
