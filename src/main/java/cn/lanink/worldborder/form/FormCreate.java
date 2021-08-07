package cn.lanink.worldborder.form;

import cn.lanink.worldborder.WorldBorder;
import cn.lanink.worldborder.border.Border;
import cn.lanink.worldborder.border.Borders;
import cn.lanink.worldborder.form.element.ResponseElementButton;
import cn.lanink.worldborder.form.windows.AdvancedFormWindowCustom;
import cn.lanink.worldborder.form.windows.AdvancedFormWindowModal;
import cn.lanink.worldborder.form.windows.AdvancedFormWindowSimple;
import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.form.element.ElementButtonImageData;
import cn.nukkit.form.element.ElementDropdown;
import cn.nukkit.form.element.ElementInput;
import cn.nukkit.form.element.ElementLabel;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;

public class FormCreate {

    public static final String PLUGIN_NAME = "§l§7[§1W§2o§3r§4l§5d§6B§ao§cu§bn§dd§9a§6r§2y§7]";

    public static void sendAdminMenu(@NotNull Player player) {
        AdvancedFormWindowSimple simple = new AdvancedFormWindowSimple(PLUGIN_NAME, "§a当前设置世界：" + player.getLevel().getName());
        Borders borders = WorldBorder.getInstance().getBorders().get(player.getLevel().getName());
        simple.addButton(new ResponseElementButton("§e添加新的边界", new ElementButtonImageData("path", "textures/ui/World"))
                .onClicked(FormCreate::sendAddBorder));
        if (borders != null && !borders.getBorders().isEmpty()) {
            simple.addButton(new ResponseElementButton("§e管理现有边界", new ElementButtonImageData("path", "textures/ui/dev_glyph_color"))
                    .onClicked(FormCreate::sendAdminBorders));
        }
        simple.addButton(new ResponseElementButton("§e重载配置", new ElementButtonImageData("path", "textures/ui/refresh_light"))
                .onClicked(cp -> Server.getInstance().dispatchCommand(cp, "WorldBorder Reload")));
        player.showFormWindow(simple);
    }

    public static void sendAddBorder(@NotNull Player player) {
        AdvancedFormWindowCustom custom = new AdvancedFormWindowCustom(PLUGIN_NAME);
        custom.addElement(new ElementDropdown("边界形状", Arrays.asList("方形", "圆形")));
        custom.addElement(new ElementInput("边界名称", "主城出生区"));
        custom.onResponded((formResponseCustom, cp) ->
                Server.getInstance().dispatchCommand(cp,
                "WorldBorder AddBorder " +
                        formResponseCustom.getDropdownResponse(0).getElementContent() + " "  +
                        formResponseCustom.getInputResponse(1)));
        player.showFormWindow(custom);
    }

    public static void sendSetBorderRadius(@NotNull Player player, @NotNull Border border) {
        AdvancedFormWindowCustom custom = new AdvancedFormWindowCustom(PLUGIN_NAME);
        custom.addElement(new ElementInput("半径", "100"));
        custom.onResponded((formResponseCustom, cp) -> {
            double radius;
            try {
                radius = Double.parseDouble(formResponseCustom.getInputResponse(0));
            } catch (Exception e) {
                try {
                    radius = Integer.parseInt(formResponseCustom.getInputResponse(0));
                } catch (Exception ignored) {
                    AdvancedFormWindowModal modal = new AdvancedFormWindowModal(PLUGIN_NAME,
                            "半径必须是数字！",
                            "返回", "返回");
                    modal.onClickedTrue(cp2 -> sendSetBorderRadius(cp2, border));
                    modal.onClickedFalse(cp2 -> sendSetBorderRadius(cp2, border));
                    modal.onClosed(cp2 -> sendSetBorderRadius(cp2, border));
                    cp.showFormWindow(modal);
                    return;
                }
            }
            if (radius <= 2) {
                AdvancedFormWindowModal modal = new AdvancedFormWindowModal(PLUGIN_NAME,
                        "你设置的半径太小啦！至少要大于2！",
                        "返回", "返回");
                modal.onClickedTrue(cp2 -> sendSetBorderRadius(cp2, border));
                modal.onClickedFalse(cp2 -> sendSetBorderRadius(cp2, border));
                modal.onClosed(cp2 -> sendSetBorderRadius(cp2, border));
                cp.showFormWindow(modal);
                return;
            }
            WorldBorder.getInstance().getPlayerSet().remove(player);
            border.setRadius(radius);
            Borders borders = WorldBorder.getInstance().getBorders(border.getLevel());
            if (borders == null) {
                borders = new Borders(border.getLevel());
                WorldBorder.getInstance().getBorders().put(border.getLevel().getName(), borders);
            }
            borders.addBorder(border);
            borders.saveConfig();
            cp.sendTitle("", "边界添加成功！");
        });
        player.showFormWindow(custom);
    }

    public static void sendAdminBorders(@NotNull Player player) {
        AdvancedFormWindowSimple simple = new AdvancedFormWindowSimple(PLUGIN_NAME, "管理边界");
        Borders borders = WorldBorder.getInstance().getBorders(player.getLevel());
        if (borders != null) {
            for (Border border : borders.getBorders()) {
                simple.addButton(new ResponseElementButton(border.getName())
                        .onClicked(cp -> sendAdminBorder(cp, border)));
            }
        }
        simple.onClosed(FormCreate::sendAdminMenu);
        player.showFormWindow(simple);
    }

    public static void sendAdminBorder(@NotNull Player player, @NotNull Border border) {
        StringBuilder builder = new StringBuilder("名称：").append(border.getName()).append("\n形状：");
        if (border.getBorderType() == Border.BorderType.SQUARE) {
            builder.append("方形\n坐标：\nMinX：").append(border.getMinX())
                    .append("\nMaxX：").append(border.getMaxX())
                    .append("\nMinZ：").append(border.getMinZ())
                    .append("\nMaxZ：").append(border.getMaxZ());
        }else {
            builder.append("圆形\n圆心坐标：\nX：").append(border.getMinX())
                    .append("\nZ：").append(border.getMinZ())
                    .append("\n半径：").append(border.getRadius());
        }
        builder.append("\n\n");
        AdvancedFormWindowSimple simple = new AdvancedFormWindowSimple(PLUGIN_NAME, builder.toString());
        simple.addButton(new ResponseElementButton("重命名").onClicked(cp -> sendAdminBorderSetName(cp, border)));
        simple.addButton(new ResponseElementButton("重设坐标")
                .onClicked(cp -> {
                    player.sendMessage("请通过放置/破坏方块的方式设置坐标");
                    if (border.getBorderType() == Border.BorderType.ROUND) {
                        player.sendTitle("", "请设置圆心");
                    }else {
                        player.sendTitle("", "请设置坐标一");
                    }
                    WorldBorder.getInstance().getPlayerSet().put(cp, border.clone());
                }));
        simple.addButton(new ResponseElementButton("删除边界")
                .onClicked(cp -> {
                    AdvancedFormWindowModal modal = new AdvancedFormWindowModal(PLUGIN_NAME,
                            "确定要删除边界： " + border.getName() + " ？",
                            "确定", "取消");
                    modal.onClickedTrue(cp2 -> {
                        Borders borders = border.getBorders();
                        borders.getBorders().remove(border);
                        borders.saveConfig();

                        AdvancedFormWindowModal modal2 = new AdvancedFormWindowModal(PLUGIN_NAME,
                                "已成功删除边界： " + border.getName() + " ！",
                                "返回", "关闭");
                        modal2.onClickedTrue(FormCreate::sendAdminBorders);
                        cp2.showFormWindow(modal2);
                    });
                    modal.onClickedFalse(cp2 -> sendAdminBorder(cp2, border));
                    modal.onClosed(cp2 -> sendAdminBorder(cp2, border));
                    player.showFormWindow(modal);
                })
        );
        simple.onClosed(FormCreate::sendAdminBorders);
        player.showFormWindow(simple);
    }

    public static void sendAdminBorderSetName(@NotNull Player player, @NotNull Border border) {
        AdvancedFormWindowCustom custom = new AdvancedFormWindowCustom(PLUGIN_NAME);
        custom.addElement(new ElementLabel("当前设置边界： " + border.getName()));
        custom.addElement(new ElementInput("新的名称", "主城出生区"));
        custom.onResponded((formResponseCustom, cp) -> {
            String newName = formResponseCustom.getInputResponse(1);
            if ("".equals(newName.trim())) {
                AdvancedFormWindowModal modal = new AdvancedFormWindowModal(PLUGIN_NAME,
                        "名称不能为空！",
                        "返回", "关闭");
                modal.onClickedTrue(cp2 -> sendAdminBorderSetName(cp2, border));
                cp.showFormWindow(modal);
                return;
            }
            border.setName(newName);
            border.getBorders().saveConfig();
            AdvancedFormWindowModal modal = new AdvancedFormWindowModal(PLUGIN_NAME,
                    "重命名成功！新名称： " + newName,
                    "返回", "关闭");
            modal.onClickedTrue(cp2 -> sendAdminBorder(cp2, border));
            cp.showFormWindow(modal);
        });
        custom.onClosed(cp -> sendAdminBorder(cp, border));
        player.showFormWindow(custom);
    }

}
