package de.evilcodez.classicserver.utils;

public class BlockMappings {

    public static int classicToAlphaBlocks(int classicId) {
        switch(classicId) {
            case 0: // air
                return 0; // 0:0
            case 1: // stone
                return 16; // 1:0
            case 2: // grass
                return 32; // 2:0
            case 3: // dirt
                return 48; // 3:0
            case 4: // cobblestone
                return 64; // 4:0
            case 5: // wood
                return 80; // 5:0
            case 6: // sapling
                return 96; // 6:0
            case 7: // bedrock
                return 112; // 7:0
            case 8: // water
                return 128; // 8:0
            case 9: // stationary_water
                return 144; // 9:0
            case 10: // lava
                return 160; // 10:0
            case 11: // stationary_lava
                return 176; // 11:0
            case 12: // sand
                return 192; // 12:0
            case 13: // gravel
                return 208; // 13:0
            case 14: // gold_ore
                return 224; // 14:0
            case 15: // iron_ore
                return 240; // 15:0
            case 16: // coal_ore
                return 256; // 16:0
            case 17: // log
                return 272; // 17:0
            case 18: // leaves
                return 288; // 18:0
            case 19: // sponge
                return 304; // 19:0
            case 20: // glass
                return 320; // 20:0
            case 21: // red_wool
                return 574; // 35:14
            case 22: // orange_wool
                return 561; // 35:1
            case 23: // yellow_wool
                return 564; // 35:4
            case 24: // lime_wool
                return 565; // 35:5
            case 25: // green_wool
                return 573; // 35:13
            case 26: // teal_wool
                return 563; // 35:3
            case 27: // aqua_blue_wool
                return 569; // 35:9
            case 28: // cyan_wool
                return 569; // 35:9
            case 29: // blue_wool
                return 571; // 35:11
            case 30: // indigo_wool
                return 570; // 35:10
            case 31: // violet_wool
                return 570; // 35:10
            case 32: // magenta_wool
                return 562; // 35:2
            case 33: // pink_wool
                return 566; // 35:6
            case 34: // black_wool
                return 575; // 35:15
            case 35: // gray_wool
                return 567; // 35:7
            case 36: // white_wool
                return 560; // 35:0
            case 37: // dandelion
                return 592; // 37:0
            case 38: // rose
                return 608; // 38:0
            case 39: // brown_mushroom
                return 624; // 39:0
            case 40: // red_mushroom
                return 640; // 40:0
            case 41: // gold_block
                return 656; // 41:0
            case 42: // iron_block
                return 672; // 42:0
            case 43: // double_slab
                return 688; // 43:0
            case 44: // slab
                return 704; // 44:0
            case 45: // brick
                return 720; // 45:0
            case 46: // tnt
                return 736; // 46:0
            case 47: // bookshelf
                return 752; // 47:0
            case 48: // mossy_cobblestone
                return 768; // 48:0
            case 49: // obsidian
                return 784; // 49:0
        }
        return 0;
    }


    public static int alphaToClassic(int alphaId) {
        switch(alphaId) {
            case 16:
                return 1;
            case 17:
                return 1;
            case 18:
                return 1;
            case 19:
                return 19;
            case 20:
                return 19;
            case 21:
                return 1;
            case 22:
                return 1;
            case 1600:
                return 21;
            case 1616:
                return 34;
            case 1632:
                return 48;
            case 1648:
                return 23;
            case 1664:
                return 37;
            case 1680:
                return 37;
            case 1696:
                return 6;
            case 1712:
                return 40;
            case 1728:
                return 45;
            case 1744:
                return 16;
            case 1760:
                return 45;
            case 1776:
                return 18;
            case 1792:
                return 40;
            case 1808:
                return 34;
            case 1824:
                return 40;
            case 1840:
                return 21;
            case 1856:
                return 13;
            case 1872:
                return 20;
            case 1888:
                return 20;
            case 192:
                return 12;
            case 193:
                return 36;
            case 1920:
                return 48;
            case 1936:
                return 12;
            case 1952:
                return 1;
            case 1968:
                return 40;
            case 1984:
                return 17;
            case 2000:
                return 5;
            case 2001:
                return 43;
            case 2002:
                return 19;
            case 2003:
                return 36;
            case 2004:
                return 36;
            case 2005:
                return 16;
            case 2016:
                return 17;
            case 2017:
                return 19;
            case 2018:
                return 38;
            case 2019:
                return 19;
            case 2020:
                return 19;
            case 2021:
                return 19;
            case 2032:
                return 25;
            case 2048:
                return 35;
            case 2064:
                return 1;
            case 208:
                return 13;
            case 2096:
                return 17;
            case 2128:
                return 24;
            case 2144:
                return 25;
            case 2160:
                return 14;
            case 2176:
                return 3;
            case 2192:
                return 5;
            case 2208:
                return 48;
            case 2224:
                return 38;
            case 2225:
                return 19;
            case 224:
                return 14;
            case 2240:
                return 40;
            case 2256:
                return 43;
            case 2272:
                return 43;
            case 2288:
                return 38;
            case 2320:
                return 34;
            case 2352:
                return 38;
            case 2368:
                return 38;
            case 2384:
                return 20;
            case 240:
                return 15;
            case 2400:
                return 20;
            case 2416:
                return 19;
            case 2432:
                return 43;
            case 2448:
                return 36;
            case 2464:
                return 19;
            case 2480:
                return 1;
            case 2481:
                return 1;
            case 2482:
                return 1;
            case 2496:
                return 1;
            case 2512:
                return 1;
            case 2528:
                return 19;
            case 2544:
                return 19;
            case 2545:
                return 36;
            case 2554:
                return 43;
            case 2555:
                return 43;
            case 2556:
                return 43;
            case 2557:
                return 43;
            case 2558:
                return 43;
            case 2559:
                return 16;
            case 2546:
                return 36;
            case 2547:
                return 1;
            case 2548:
                return 19;
            case 2549:
                return 36;
            case 2550:
                return 36;
            case 2551:
                return 16;
            case 2552:
                return 1;
            case 2553:
                return 43;
            case 256:
                return 16;
            case 2560:
                return 38;
            case 2561:
                return 19;
            case 2570:
                return 43;
            case 2571:
                return 26;
            case 2572:
                return 33;
            case 2573:
                return 41;
            case 2574:
                return 22;
            case 2575:
                return 20;
            case 2562:
                return 1;
            case 2563:
                return 1;
            case 2564:
                return 19;
            case 2565:
                return 41;
            case 2566:
                return 19;
            case 2567:
                return 35;
            case 2568:
                return 1;
            case 2569:
                return 43;
            case 2576:
                return 18;
            case 2577:
                return 18;
            case 2592:
                return 36;
            case 2593:
                return 43;
            case 2608:
                return 1;
            case 2624:
                return 43;
            case 2640:
                return 48;
            case 2672:
                return 38;
            case 2688:
                return 35;
            case 2689:
                return 16;
            case 2690:
                return 35;
            case 2704:
                return 19;
            case 272:
                return 17;
            case 273:
                return 25;
            case 274:
                return 35;
            case 275:
                return 43;
            case 2720:
                return 36;
            case 2736:
                return 38;
            case 2737:
                return 38;
            case 2746:
                return 38;
            case 2747:
                return 19;
            case 2748:
                return 19;
            case 2749:
                return 19;
            case 2750:
                return 38;
            case 2751:
                return 1;
            case 2738:
                return 38;
            case 2739:
                return 38;
            case 2740:
                return 19;
            case 2741:
                return 19;
            case 2742:
                return 38;
            case 2743:
                return 19;
            case 2744:
                return 38;
            case 2745:
                return 19;
            case 2752:
                return 16;
            case 2768:
                return 43;
            case 2784:
                return 19;
            case 2800:
                return 38;
            case 2801:
                return 38;
            case 2802:
                return 19;
            case 2803:
                return 19;
            case 2804:
                return 19;
            case 2805:
                return 38;
            case 2848:
                return 20;
            case 2864:
                return 46;
            case 2865:
                return 46;
            case 2866:
                return 46;
            case 288:
                return 18;
            case 289:
                return 18;
            case 290:
                return 18;
            case 291:
                return 1;
            case 2880:
                return 21;
            case 2896:
                return 46;
            case 2912:
                return 21;
            case 2928:
                return 39;
            case 2944:
                return 40;
            case 2960:
                return 34;
            case 2976:
                return 39;
            case 2992:
                return 34;
            case 3008:
                return 34;
            case 3024:
                return 20;
            case 304:
                return 19;
            case 305:
                return 12;
            case 3040:
                return 40;
            case 3056:
                return 39;
            case 3072:
                return 40;
            case 3088:
                return 19;
            case 3104:
                return 38;
            case 3120:
                return 38;
            case 3136:
                return 19;
            case 3152:
                return 1;
            case 3168:
                return 34;
            case 3184:
                return 20;
            case 32:
                return 3;
            case 320:
                return 20;
            case 3200:
                return 34;
            case 3216:
                return 13;
            case 3232:
                return 13;
            case 3248:
                return 13;
            case 3264:
                return 13;
            case 3280:
                return 34;
            case 3296:
                return 12;
            case 3312:
                return 25;
            case 3328:
                return 3;
            case 336:
                return 1;
            case 3360:
                return 1;
            case 3376:
                return 48;
            case 3392:
                return 26;
            case 3408:
                return 22;
            case 3424:
                return 40;
            case 3440:
                return 40;
            case 3456:
                return 12;
            case 3488:
                return 34;
            case 352:
                return 43;
            case 368:
                return 34;
            case 3760:
                return 35;
            case 3776:
                return 48;
            case 3792:
                return 32;
            case 3808:
                return 26;
            case 3824:
                return 23;
            case 384:
                return 12;
            case 385:
                return 19;
            case 386:
                return 19;
            case 3840:
                return 23;
            case 3856:
                return 33;
            case 3872:
                return 34;
            case 3888:
                return 35;
            case 3904:
                return 27;
            case 3920:
                return 29;
            case 3936:
                return 29;
            case 3952:
                return 47;
            case 3968:
                return 3;
            case 3984:
                return 21;
            case 400:
                return 20;
            case 4000:
                return 40;
            case 4016:
                return 16;
            case 4017:
                return 22;
            case 4026:
                return 29;
            case 4027:
                return 29;
            case 4028:
                return 25;
            case 4029:
                return 25;
            case 4030:
                return 21;
            case 4031:
                return 39;
            case 4018:
                return 30;
            case 4019:
                return 27;
            case 4020:
                return 23;
            case 4021:
                return 24;
            case 4022:
                return 33;
            case 4023:
                return 20;
            case 4024:
                return 1;
            case 4025:
                return 27;
            case 4032:
                return 16;
            case 4033:
                return 22;
            case 4042:
                return 30;
            case 4043:
                return 29;
            case 4044:
                return 3;
            case 4045:
                return 3;
            case 4046:
                return 21;
            case 4047:
                return 34;
            case 4034:
                return 32;
            case 4035:
                return 26;
            case 4036:
                return 23;
            case 4037:
                return 24;
            case 4038:
                return 33;
            case 4039:
                return 20;
            case 4040:
                return 14;
            case 4041:
                return 27;
            case 4080:
                return 20;
            case 432:
                return 22;
            case 448:
                return 13;
            case 464:
                return 3;
            case 48:
                return 3;
            case 49:
                return 30;
            case 50:
                return 43;
            case 480:
                return 16;
            case 496:
                return 25;
            case 497:
                return 19;
            case 498:
                return 18;
            case 512:
                return 25;
            case 528:
                return 45;
            case 544:
                return 43;
            case 560:
                return 36;
            case 561:
                return 22;
            case 570:
                return 31;
            case 571:
                return 29;
            case 572:
                return 25;
            case 573:
                return 25;
            case 574:
                return 21;
            case 575:
                return 34;
            case 562:
                return 32;
            case 563:
                return 26;
            case 564:
                return 23;
            case 565:
                return 24;
            case 566:
                return 33;
            case 567:
                return 35;
            case 568:
                return 35;
            case 569:
                return 28;
            case 592:
                return 37;
            case 608:
                return 38;
            case 609:
                return 38;
            case 610:
                return 38;
            case 611:
                return 38;
            case 612:
                return 38;
            case 613:
                return 38;
            case 614:
                return 38;
            case 615:
                return 38;
            case 616:
                return 38;
            case 624:
                return 39;
            case 64:
                return 4;
            case 640:
                return 40;
            case 656:
                return 41;
            case 672:
                return 1;
            case 688:
                return 43;
            case 689:
                return 12;
            case 690:
                return 5;
            case 691:
                return 1;
            case 692:
                return 45;
            case 693:
                return 16;
            case 694:
                return 40;
            case 695:
                return 1;
            case 704:
                return 44;
            case 705:
                return 14;
            case 706:
                return 17;
            case 707:
                return 14;
            case 708:
                return 19;
            case 709:
                return 38;
            case 710:
                return 34;
            case 711:
                return 1;
            case 720:
                return 45;
            case 736:
                return 46;
            case 752:
                return 47;
            case 768:
                return 48;
            case 784:
                return 49;
            case 80:
                return 5;
            case 81:
                return 43;
            case 82:
                return 19;
            case 83:
                return 36;
            case 84:
                return 36;
            case 85:
                return 16;
            case 816:
                return 41;
            case 832:
                return 49;
            case 848:
                return 5;
            case 880:
                return 40;
            case 896:
                return 15;
            case 912:
                return 26;
            case 928:
                return 17;
            case 944:
                return 3;
            case 96:
                return 6;
            case 97:
                return 34;
            case 98:
                return 3;
            case 99:
                return 43;
            case 100:
                return 25;
            case 101:
                return 25;
            case 960:
                return 17;
            case 1024:
                return 17;
            case 1056:
                return 13;
            case 1072:
                return 16;
            case 1104:
                return 40;
            case 112:
                return 34;
            case 1120:
                return 20;
            case 1136:
                return 16;
            case 1152:
                return 25;
            case 1168:
                return 13;
            case 1184:
                return 13;
            case 1232:
                return 39;
            case 1248:
                return 43;
            case 1264:
                return 26;
            case 1280:
                return 43;
            case 1296:
                return 18;
            case 1312:
                return 35;
            case 1328:
                return 12;
            case 1344:
                return 17;
            case 1360:
                return 25;
            case 1376:
                return 46;
            case 1392:
                return 21;
            case 1408:
                return 20;
            case 1424:
                return 47;
            case 1456:
                return 22;
            case 1472:
                return 19;
            case 1488:
                return 1;
            case 1504:
                return 1;
            case 1520:
                return 16;
            case 1521:
                return 22;
            case 1530:
                return 29;
            case 1531:
                return 29;
            case 1532:
                return 37;
            case 1533:
                return 6;
            case 1534:
                return 21;
            case 1535:
                return 39;
            case 1522:
                return 30;
            case 1523:
                return 27;
            case 1524:
                return 23;
            case 1525:
                return 24;
            case 1526:
                return 21;
            case 1527:
                return 39;
            case 1528:
                return 34;
            case 1529:
                return 27;
            case 1536:
                return 37;
            case 1552:
                return 1;
            case 1553:
                return 1;
            case 1554:
                return 16;
            case 1555:
                return 36;
            case 1556:
                return 19;
            case 1557:
                return 19;
            case 1568:
                return 16;
            case 1569:
                return 1;
            case 1570:
                return 1;
            case 1571:
                return 19;
            case 1584:
                return 3;
            case 0:
                return 0;
            case 128:
                return 29;
            case 144:
                return 29;
            case 160:
                return 10;
            case 176:
                return 11;
        }
        return 3;
    }
}
