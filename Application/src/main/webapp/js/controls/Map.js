/* 
 * Map control.
 * Requires SystemDao.js, OpenLayers.js, ScaleBar.js, ext-base.js, ext-all.js,
 * GeoExt.js, Google API, PropertyDao.js, RefDataDao.js
 */
var Controls = Controls || {};

// Map control
Controls.Map = function (controlId, targetElementId, options) {
    validateControl(controlId, targetElementId);

    var controlVarId = "__control_map_" + controlId;
    var that = this;
    window[controlVarId] = this;
    options = options ? options : {};

    // Enable cors for IE
    $.support.cors = true;

    // If plot is missing, show default point
    var wkt = new OpenLayers.Format.WKT();

    // Boolean flag, indicating whether map can be edited. If false, editing tools will be hidden
    var editable = false;

    // Boolean flag, indicating whether CS is offline or not
    var isOffline = false;

    // Map toolbar reference
    var mapToolbar;

    // Map legend
    var mapLegendTree;

    // Selected legend item
    var selectedNode = null;

    // Boolean flag, used to indicate whether map editing is on or off
    var enableMapEditing = false;

    // OL Map
    var map;

    // Map container
    var mapPanelContainer;

    // Idicates whether map was rendered or not
    var isRendered = false;

    // Initial layers
    var layers = [];

    // Parcels to show/edit
    var parcels = [];

    // Map height
    var mapHeight = options.mapHeight ? options.mapHeight : 500;

    // Application
    var app = options.application ? options.application : null;

    // Parcel id
    var parcelId = options.parcelId ? options.parcelId : null;

    // Default feature source CRS
    var sourceCrs = "EPSG:4326";
    Proj4js.defs["EPSG:4326"] = "+proj=longlat +datum=WGS84 +no_defs";

    // Default destination CRS
    var destCrs = "EPSG:3857";
    Proj4js.defs["EPSG:3857"] = "+proj=merc +a=6378137 +b=6378137 +lat_ts=0.0 +lon_0=0.0 +x_0=0.0 +y_0=0 +k=1.0 +units=m +nadgrids=@null +wktext  +no_defs";

    // CSR cutomized for printing parcel map
    var printCrs = null;

    var mapPanel;

    var mapToolbarItems = [];

    // Map max extent bounds, used for full extent action
    var maxExtentBounds = null;

    // Initial zoom, required for proper zooming when rendering a map
    var initialZoomBounds = null;

    // Snapping layers
    var snappingLayers = [];

    var editedParcel = null;

    // Layer containing claims, selected for snapping. These claims are read-only and layer is not displayed in the legend
    var layerSnappingFeatures = new Controls.Map.Layer.VectorLayer(
            Controls.Map.LAYER_IDS.SNAPPING_FEATURES,
            "Snapping",
            {
                isEditable: false,
                displayInLayerSwitcher: false,
                styleMap: Controls.Map.Styles.styleSnappingClaim,
                virtualNodeStyle: Controls.Map.Styles.styleClaimNode
            });

    var layerSelectedParcels = null;

    // custom layer node UI class
    var LayerNodeUI = Ext.extend(
            GeoExt.tree.LayerNodeUI,
            new GeoExt.tree.TreeNodeUIEventMixin()
            );

    this.init = function () {
        // Wrap map control
        $("#" + targetElementId).wrap("<div class='mapCtrlWrapper'></div>");

        // Get map settings
        SystemDao.getMapSettings(function (mapSettings) {
            isOffline = mapSettings.offlineMode;
            if (!isNullOrEmpty(mapSettings.printingSrs)) {
                printCrs = "EPSG:" + mapSettings.printingSrs;
                Proj4js.defs[printCrs] = mapSettings.printingSrsProj4;
            }

            if (!isNull(mapSettings.mapExtent)) {
                maxExtentBounds = wkt.read(mapSettings.mapExtent);
            } else {
                // Set default
                maxExtentBounds = wkt.read("Polygon ((35.674 -7.759, 35.713 -7.759, 35.713 -7.786, 35.674 -7.786, 35.674 -7.759))");
            }
            maxExtentBounds.geometry.transform(sourceCrs, destCrs);
            maxExtentBounds = maxExtentBounds.geometry.getBounds();
            initialZoomBounds = maxExtentBounds;

            // Add configured layers
            if (!isNull(mapSettings.layers) && mapSettings.layers.length > 0) {
                for (var i = 0; i < mapSettings.layers.length; i++) {
                    var wmsLayer = new OpenLayers.Layer.WMS(
                            mapSettings.layers[i].title,
                            mapSettings.layers[i].url,
                            addLayerOptions(mapSettings.layers[i], {layers: mapSettings.layers[i].name}, true),
                            addLayerOptions(mapSettings.layers[i], {minResolution: 0.001, maxResolution: 200}, false)
                            );
                    wmsLayer.legendOptions = addLegendOptions(mapSettings.layers[i]);
                    layers.push(wmsLayer);
                }
            }

            // Get plots by application
            if (!isNull(app)) {
                // Allow editing if applicaion is pending and user has appropriate rights
                if (app.statusCode === Global.STATUS.pending && Global.USER_PERMISSIONS.canManageParcels) {
                    editable = true;

                    // Load control template
                    $.get(Global.APP_ROOT + '/js/templates/ControlMapDialogs.html', function (tmpl) {
                        var template = Handlebars.compile(tmpl);
                        $('#' + targetElementId).parent().after(template({id: controlVarId}));
                        bindDateFields();

                        // Localize
                        $("#" + controlVarId + "_ParcelDialog").i18n();
                    });
                }

                PropertyDao.getCreateParcelsByApplication(app.id, function (parcelsList) {
                    if (!isNull(parcelsList)) {
                        parcels = parcelsList;
                    }
                }, null, function () {
                    postInit();
                });
            } else if (!isNull(parcelId)) {
                PropertyDao.getParcel(parcelId, function (parcel) {
                    if (!isNull(parcel)) {
                        parcels.push(parcel);
                    }
                }, null, function () {
                    postInit();
                });
            } else {
                postInit();
            }
        });
    };

    var postInit = function () {
        // if application or parcel are provided
        if (!isNull(app) || !isNull(parcelId)) {
            layerSelectedParcels = new Controls.Map.Layer.VectorLayer(
                    Controls.Map.LAYER_IDS.CURRENT_CLAIM,
                    $.i18n("map-control-current-plot"),
                    {
                        allowMultipleFeatures: true,
                        allowPoint: false,
                        allowLine: false,
                        styleMap: Controls.Map.Styles.styleMapClaim,
                        isEditable: editable,
                        virtualNodeStyle: Controls.Map.Styles.styleClaimNode,
                        editFeatureFunc: editParcelAttributes
                    });
            layers.push(layerSelectedParcels);
            snappingLayers.push(layerSelectedParcels);
            snappingLayers.push(layerSnappingFeatures);

            if (parcels.length > 0) {
                for (var i = 0; i < parcels.length; i++) {
                    var parcel = wkt.read(parcels[i].geom);
                    parcel.geometry.transform(sourceCrs, destCrs);
                    parcel.attributes = $.extend(true, {}, parcels[i]);
                    layerSelectedParcels.addFeatures(parcel);

                    // Update parcels to make sure it has same precision of coordinates
                    var clone = parcel.clone();
                    clone.geometry.transform(destCrs, sourceCrs);
                    parcels[i].geom = wkt.write(clone);
                }
                initialZoomBounds = layerSelectedParcels.getDataExtent();
            }
        }

        // Init map
        map = new OpenLayers.Map(controlId + "_map", {
            div: controlId + "_map",
            allOverlays: false,
            projection: destCrs,
            displayProjection: isNullOrEmpty(printCrs) ? sourceCrs : printCrs,
            maxExtentBounds: maxExtentBounds,
            initialZoomBounds: initialZoomBounds,
            units: 'm',
            numZoomLevels: 22
        });

        try {
            if (!isOffline) {
                var gsat = new OpenLayers.Layer.Google($.i18n("map-control-google-earth"), {type: google.maps.MapTypeId.SATELLITE, numZoomLevels: 22});
                var gmap = new OpenLayers.Layer.Google($.i18n("map-control-google-map"), {numZoomLevels: 20, visibility: false});
                map.addLayers([gsat, gmap]);
            }
        } catch (e) {

        }

        map.events.register('addlayer', map, handleAddLayer);

        if (layers.length > 0) {
            map.addLayers(layers);
        }

        // Check for base layers
        var hasBaseLayer = false;
        if (map.layers.length > 0) {
            for (var i = 0; i < map.layers.length; i++) {
                if (map.layers[i].isBaseLayer === true) {
                    hasBaseLayer = true;
                    break;
                }
            }
        }

        if (!hasBaseLayer) {
            // Add dummy base layer
            var emptyBase = new OpenLayers.Layer("Empty", {isBaseLayer: true, minResolution: 0.001, maxResolution: 200});
            map.addLayers([emptyBase]);
            map.setLayerIndex(emptyBase, 0);
        }

        map.events.register('changelayer', map, handleLayerChange);

        mapPanel = new GeoExt.MapPanel({
            region: 'center',
            zoom: 15,
            map: map
        });

        mapLegendTree = new Ext.tree.TreePanel({
            region: 'west',
            title: $.i18n("map-control-layers"),
            width: 250,
            autoScroll: true,
            listeners: {
                beforeclick: nodeSelectionHandler
            },
            collapsible: true,
            split: true,
            enableDD: true,
            // apply the tree node component plugin to layer nodes
            plugins: [{
                    ptype: "gx_treenodecomponent"
                }],
            loader: {
                applyLoader: false,
                uiProviders: {
                    "custom_ui": LayerNodeUI
                }
            },
            root: {
                nodeType: "gx_layercontainer",
                loader: {
                    baseAttrs: {
                        uiProvider: "custom_ui"
                    },
                    createNode: function (attr) {
                        if (attr.layer.CLASS_NAME === 'OpenLayers.Layer.WMS') {
                            attr.component = {
                                xtype: "gx_wmslegend",
                                baseParams: {
                                    LEGEND_OPTIONS: attr.layer.legendOptions === 'undefined' ? '' : attr.layer.legendOptions
                                },
                                layerRecord: mapPanel.layers.getByLayer(attr.layer),
                                showTitle: false,
                                cls: "legend"
                            };
                        } else {
                            if (attr.layer.CLASS_NAME === 'OpenLayers.Layer.Vector') {
                                attr.component = {
                                    xtype: "gx_vectorlegend",
                                    untitledPrefix: "",
                                    layerRecord: mapPanel.layers.getByLayer(attr.layer),
                                    showTitle: false,
                                    cls: "legend",
                                    clickableTitle: true,
                                    selectOnClick: true,
                                    node: attr,
                                    listeners: {
                                        ruleselected: function (legend, event) {
                                            nodeSelectionHandler(legend.node, event, true);
                                        },
                                        ruleunselected: function (legend, event) {
                                            nodeSelectionHandler(legend.node, event, true);
                                        }
                                    }
                                };
                            } else {
                                attr.component = {
                                    untitledPrefix: "",
                                    layerRecord: mapPanel.layers.getByLayer(attr.layer),
                                    showTitle: false,
                                    cls: "legend"
                                };
                            }
                        }
                        return GeoExt.tree.LayerLoader.prototype.createNode.call(this, attr);
                    }
                }
            },
            rootVisible: false,
            lines: false
        });

        Ext.QuickTips.init();

        mapToolbarItems.push({
            id: Controls.Map.TOOLBAR_BUTTON_IDS.ZOOM_TO_EXTENT,
            iconCls: 'zoomToExtentIcon',
            text: $.i18n("map-control-zoom-to-extent"),
            tooltip: $.i18n("map-control-zoom-to-extent"),
            handler: function () {
                map.zoomToExtent(maxExtentBounds);
            }
        });
        mapToolbarItems.push(new GeoExt.Action({
            id: Controls.Map.TOOLBAR_BUTTON_IDS.ZOOM_IN,
            control: new OpenLayers.Control.ZoomBox({out: false}),
            iconCls: 'zoomInIcon',
            toggleGroup: "draw",
            group: "draw",
            map: map,
            text: $.i18n("map-control-zoom-in"),
            tooltip: $.i18n("map-control-zoom-in")
        }));
        mapToolbarItems.push(new GeoExt.Action({
            id: Controls.Map.TOOLBAR_BUTTON_IDS.ZOOM_OUT,
            control: new OpenLayers.Control.ZoomBox({out: true}),
            toggleGroup: "draw",
            group: "draw",
            iconCls: 'zoomOutIcon',
            map: map,
            text: $.i18n("map-control-zoom-out"),
            tooltip: $.i18n("map-control-zoom-out")
        }));

        mapToolbarItems.push("-");

        mapToolbarItems.push(new GeoExt.Action({
            id: Controls.Map.TOOLBAR_BUTTON_IDS.PAN,
            control: new OpenLayers.Control(),
            toggleGroup: "draw",
            group: "draw",
            iconCls: 'panIcon',
            map: map,
            text: $.i18n("map-control-pan"),
            tooltip: $.i18n("map-control-pan")
        }));

        var claimInfoControl = new OpenLayers.Control();
        OpenLayers.Util.extend(claimInfoControl, {
            draw: function () {
                this.clickHandler = new OpenLayers.Handler.Click(claimInfoControl,
                        {click: handleClaimInfoClick},
                        {delay: 0, single: true, double: false, stopSingle: false, stopDouble: true});

            },
            activate: function () {
                return this.clickHandler.activate() &&
                        OpenLayers.Control.prototype.activate.apply(this, arguments);
            },
            deactivate: function () {
                var deactivated = false;
                if (OpenLayers.Control.prototype.deactivate.apply(this, arguments)) {
                    this.clickHandler.deactivate();
                    deactivated = true;
                }
                return deactivated;
            },
            CLASS_NAME: "Controls.Map.Control.ClaimInfo"
        });

        mapToolbarItems.push(new GeoExt.Action({
            id: Controls.Map.TOOLBAR_BUTTON_IDS.CLAIM_INFO,
            control: claimInfoControl,
            iconCls: 'informationIcon',
            map: map,
            toggleGroup: "draw",
            group: "draw",
            pressed: true,
            text: $.i18n("map-control-claim-info"),
            tooltip: $.i18n("map-control-claim-info")
        }));

        if (editable) {
            // Define default layer for editing 
            var defaultEditingLayer = new OpenLayers.Layer.Vector("", {});

            // Enable editing tools
            mapToolbarItems.push("-");

            mapToolbarItems.push({
                id: Controls.Map.TOOLBAR_BUTTON_IDS.EDIT_MAP,
                iconCls: 'editMapIcon',
                text: $.i18n("map-control-edit-map"),
                tooltip: $.i18n("map-control-edit-map"),
                enableToggle: true,
                toggleHandler: onMapEditToggle,
                pressed: false
            });

            mapToolbarItems.push("-");

            mapToolbarItems.push({
                id: Controls.Map.TOOLBAR_BUTTON_IDS.IMPORT_POINTS,
                iconCls: 'importPointsIcon',
                editingTool: true,
                disabled: true,
                toggleGroup: "draw",
                group: "draw",
                text: $.i18n("map-control-create-from-coords"),
                tooltip: $.i18n("map-control-create-from-coords"),
                handler: onImportPointsClick
            });

            mapToolbarItems.push(new GeoExt.Action({
                id: Controls.Map.TOOLBAR_BUTTON_IDS.DRAW_POLYGON,
                control: new Controls.Map.Control.DrawFeature(defaultEditingLayer,
                        OpenLayers.Handler.Polygon,
                        {handlerOptions: {holeModifier: "altKey"}, featureAdded: featureAdded}),
                iconCls: 'polygonIcon',
                map: map,
                editingTool: true,
                toggleGroup: "draw",
                group: "draw",
                disabled: true,
                text: $.i18n("map-control-draw-polygon"),
                tooltip: $.i18n("map-control-draw-polygon")
            }));

//            mapToolbarItems.push(new GeoExt.Action({
//                id: Controls.Map.TOOLBAR_BUTTON_IDS.DRAW_LINE,
//                control: new Controls.Map.Control.DrawFeature(defaultEditingLayer,
//                        OpenLayers.Handler.Path, {handlerOptions: {}, featureAdded: featureAdded}),
//                iconCls: 'polylineIcon',
//                map: map,
//                editingTool: true,
//                toggleGroup: "draw",
//                group: "draw",
//                disabled: true,
//                text: $.i18n("map-control-draw-line"),
//                tooltip: $.i18n("map-control-draw-line")
//            }));
//
//            mapToolbarItems.push(new GeoExt.Action({
//                id: Controls.Map.TOOLBAR_BUTTON_IDS.DRAW_POINT,
//                control: new OpenLayers.Control.DrawFeature(defaultEditingLayer,
//                        OpenLayers.Handler.Point, {handlerOptions: {}, featureAdded: featureAdded}),
//                iconCls: 'pointIcon',
//                map: map,
//                editingTool: true,
//                toggleGroup: "draw",
//                group: "draw",
//                disabled: true,
//                text: $.i18n("map-control-draw-point"),
//                tooltip: $.i18n("map-control-draw-point")
//            }));

            mapToolbarItems.push("-");

            mapToolbarItems.push(new GeoExt.Action({
                id: Controls.Map.TOOLBAR_BUTTON_IDS.EDIT_SHAPE,
                control: new OpenLayers.Control.ModifyFeature(defaultEditingLayer, null),
                iconCls: 'shapeEditIcon',
                map: map,
                editingTool: true,
                toggleGroup: "draw",
                group: "draw",
                disabled: true,
                text: $.i18n("map-control-edit-shape"),
                tooltip: $.i18n("map-control-edit-shape")
            }));

            mapToolbarItems.push(new GeoExt.Action({
                id: Controls.Map.TOOLBAR_BUTTON_IDS.DELETE_FEATURE,
                control: new OpenLayers.Control.SelectFeature(defaultEditingLayer,
                        {clickout: true, multiple: false, hover: true, box: false,
                            clickFeature: function (feature) {
                                this.unselect(feature);
                                if (confirm($.i18n("map-control-confirm-feature-delete"))) {
                                    this.layer.removeFeatures(feature);
                                    customizeMapToolbar();
                                }
                            }
                        }
                ),
                iconCls: 'featureDeleteIcon',
                map: map,
                editingTool: true,
                toggleGroup: "draw",
                group: "draw",
                disabled: true,
                text: $.i18n("map-control-delete-shape"),
                tooltip: $.i18n("map-control-delete-shape")
            }));

            mapToolbarItems.push(new GeoExt.Action({
                id: Controls.Map.TOOLBAR_BUTTON_IDS.EDIT_FEATURE,
                control: new OpenLayers.Control.SelectFeature(defaultEditingLayer,
                        {clickout: true, multiple: false, hover: true, box: false,
                            clickFeature: function (feature) {
                                this.unselect(feature);
                                if (typeof this.layer.editFeatureFunc !== 'undefined') {
                                    this.layer.editFeatureFunc(feature);
                                }
                            }
                        }
                ),
                iconCls: 'featureEditIcon',
                map: map,
                editingTool: true,
                toggleGroup: "draw",
                group: "draw",
                disabled: true,
                text: $.i18n("map-control-edit-properties"),
                tooltip: $.i18n("map-control-edit-properties")
            }));

            mapToolbarItems.push("-");

            mapToolbarItems.push(new GeoExt.Action({
                id: Controls.Map.TOOLBAR_BUTTON_IDS.SNAP,
                control: new OpenLayers.Control.Snapping({
                    layer: defaultEditingLayer,
                    targets: snappingLayers,
                    greedy: false
                }),
                iconCls: 'snapIcon',
                map: map,
                toggleHandler: snapClicked,
                editingTool: true,
                enableToggle: true,
                disabled: true,
                text: $.i18n("map-control-snap"),
                tooltip: $.i18n("map-control-snap")
            }));

            var selectSnapFeatureControl = new OpenLayers.Control();
            OpenLayers.Util.extend(selectSnapFeatureControl, {
                draw: function () {
                    this.clickHandler = new OpenLayers.Handler.Click(selectSnapFeatureControl,
                            {click: handleSelectSnapFeatureClick},
                            {delay: 0, single: true, double: false, stopSingle: false, stopDouble: true});

                },
                activate: function () {
                    return this.clickHandler.activate() &&
                            OpenLayers.Control.prototype.activate.apply(this, arguments);
                },
                deactivate: function () {
                    var deactivated = false;
                    if (OpenLayers.Control.prototype.deactivate.apply(this, arguments)) {
                        this.clickHandler.deactivate();
                        deactivated = true;
                    }
                    return deactivated;
                },
                CLASS_NAME: "Controls.Map.Control.SelectSnapFeature"
            });

            mapToolbarItems.push(new GeoExt.Action({
                id: Controls.Map.TOOLBAR_BUTTON_IDS.SNAP_SELECT,
                control: selectSnapFeatureControl,
                iconCls: 'selectSnapIcon',
                map: map,
                editingTool: true,
                toggleGroup: "draw",
                group: "draw",
                disabled: true,
                text: $.i18n("map-control-snap-select"),
                tooltip: $.i18n("map-control-snap-select")
            }));
        }

        mapToolbarItems.push("-");
        mapToolbarItems.push(new Ext.Action({
            id: Controls.Map.TOOLBAR_BUTTON_IDS.MAXIMIZE_MAP,
            iconCls: 'maximizeIcon',
            enableToggle: true,
            text: $.i18n("map-control-maximize"),
            tooltip: $.i18n("map-control-maximize-title"),
            toggleHandler: maximizeMap
        }));

        mapToolbar = new Ext.Toolbar({
            enableOverflow: true,
            items: mapToolbarItems
        });

        var mapStatusBar = new Ext.Toolbar({
            items: [new Ext.form.Label({id: controlVarId + "_lblScaleBar", text: ""}),
                '->',
                new Ext.form.Label({id: controlVarId + "_lblMapMousePosition", text: ""}),
                {xtype: 'tbspacer', width: 10}
            ]
        });

        mapPanelContainer = new Ext.Panel({
            layout: 'border',
            height: mapHeight,
            tbar: mapToolbar,
            bbar: mapStatusBar,
            items: [mapLegendTree, mapPanel]
        });

        mapLegendTree.on("afterrender", function (o) {
            // Enable map editing by default
            if (editable) {
                toggleMapEditing(true);
                enableDisableToolbarButton(Controls.Map.TOOLBAR_BUTTON_IDS.SNAP, true);
                toggleToolbarButton(Controls.Map.TOOLBAR_BUTTON_IDS.SNAP, true);
                if (!isNull(mapLegendTree.nodeHash)) {
                    for (var property in mapLegendTree.nodeHash) {
                        if (mapLegendTree.nodeHash.hasOwnProperty(property)) {
                            var node = mapLegendTree.nodeHash[property];
                            if (!isNull(node.attributes.layer)) {
                                if (node.attributes.layer.id === Controls.Map.LAYER_IDS.CURRENT_CLAIM) {
                                    nodeSelectionHandler(node, null, true);
                                }
                            }
                        }
                    }
                }
            }
        });

        renderMap();
    };

    var editParcelAttributes = function (feature) {
        if (isNull(feature)) {
            return;
        }

        if (isNull(feature.attributes) || typeof feature.attributes.uka === 'undefined') {
            feature.attributes = new PropertyDao.Parcel();
            feature.attributes.applicationId = app.id;
        }

        editedParcel = feature;

        if ($("#" + controlVarId + "_ParcelDialog").length) {
            $("#" + controlVarId + "_ParcelDialog").modal('show');

            // Text fields
            $("#" + controlVarId + "_lblUka").text("#" + String.empty(feature.attributes.uka));
            if (isNull(feature.attributes.surveyDate)) {
                $("#" + controlVarId + "_txtSurveyDate").val("");
            } else {
                $("#" + controlVarId + "_txtSurveyDate").val(dateFormat(feature.attributes.surveyDate));
            }
            $("#" + controlVarId + "_txtParcelAddress").val(String.empty(feature.attributes.address));
            $("#" + controlVarId + "_txtParcelComments").val(String.empty(feature.attributes.comment));

            // Load drop down lists
            $("#" + controlVarId + "_cbxHamlets").empty();
            $("#" + controlVarId + "_cbxVillages").empty();
            $("#" + controlVarId + "_cbxDistricts").empty();
            $("#" + controlVarId + "_cbxRegions").empty();
            $("#" + controlVarId + "_cbxLandTypes").empty();

            RefDataDao.getActiveRecords(RefDataDao.REF_DATA_TYPES.LandType.type, function (landTypes) {
                if (!isNull(landTypes)) {
                    populateSelectList(landTypes, controlVarId + "_cbxLandTypes", true);
                    if (!isNullOrEmpty(feature.attributes.landTypeCode)) {
                        $("#" + controlVarId + "_cbxLandTypes").val(feature.attributes.landTypeCode);
                    }
                }
            }, null, null, true, true);

            if (!isNull(feature.attributes.hamletCode)) {
                RefDataDao.getHamletsByHamlet(feature.attributes.hamletCode, function (hamlets) {
                    if (!isNull(hamlets)) {
                        populateSelectList(hamlets, controlVarId + "_cbxHamlets");

                        // Search for hamlet and select it
                        var hamlet = null;
                        $.each(hamlets, function (index, item) {
                            if (item.code === feature.attributes.hamletCode) {
                                hamlet = item;
                                $("#" + controlVarId + "_cbxHamlets").val(hamlet.code);
                                return false;
                            }
                        });

                        // If hamlet found, populate villages
                        if (!isNull(hamlet)) {
                            RefDataDao.getVillagesByVillage(hamlet.villageCode, function (villages) {
                                if (!isNull(villages)) {
                                    populateSelectList(villages, controlVarId + "_cbxVillages");

                                    // Search for village and select it
                                    var village = null;
                                    $.each(villages, function (index, item) {
                                        if (item.code === hamlet.villageCode) {
                                            village = item;
                                            $("#" + controlVarId + "_cbxVillages").val(village.code);
                                            return false;
                                        }
                                    });

                                    // If village found, populate districts
                                    if (!isNull(village)) {
                                        RefDataDao.getDistrictsByDistrict(village.districtCode, function (districts) {
                                            if (!isNull(districts)) {
                                                populateSelectList(districts, controlVarId + "_cbxDistricts");

                                                // Search for district and select it
                                                var district = null;
                                                $.each(districts, function (index, item) {
                                                    if (item.code === village.districtCode) {
                                                        district = item;
                                                        $("#" + controlVarId + "_cbxDistricts").val(district.code);
                                                        return false;
                                                    }
                                                });

                                                // If village found, populate regions
                                                if (!isNull(district)) {
                                                    RefDataDao.getActiveRecords(RefDataDao.REF_DATA_TYPES.Region.type, function (regions) {
                                                        if (!isNull(regions)) {
                                                            populateSelectList(regions, controlVarId + "_cbxRegions");

                                                            $.each(regions, function (index, item) {
                                                                if (item.code === district.regionCode) {
                                                                    $("#" + controlVarId + "_cbxRegions").val(district.regionCode);
                                                                    return false;
                                                                }
                                                            });
                                                        }
                                                    }, null, null, true, true);
                                                }
                                            }
                                        });
                                    }
                                }
                            });
                        }
                    }
                });
            } else {
                // Load regions only
                RefDataDao.getActiveRecords(RefDataDao.REF_DATA_TYPES.Region.type, function (regions) {
                    if (!isNull(regions)) {
                        populateSelectList(regions, controlVarId + "_cbxRegions");
                    }
                }, null, null, true, true);
            }
        } else {
            alertWarningMessage($.i18n("err-comp-loading"));
            return;
        }
    };

    this.fillDistricts = function () {
        $("#" + controlVarId + "_cbxDistricts").empty();
        var regionCode = $("#" + controlVarId + "_cbxRegions").val();

        if (!isNullOrEmpty(regionCode))
            RefDataDao.getDistrictsByRegion(regionCode, function (districts) {
                if (!isNull(districts)) {
                    populateSelectList(districts, controlVarId + "_cbxDistricts");
                }
            });
    };

    this.fillVillages = function () {
        $("#" + controlVarId + "_cbxVillages").empty();
        var districtCode = $("#" + controlVarId + "_cbxDistricts").val();

        if (!isNullOrEmpty(districtCode))
            RefDataDao.getVillagesByDistrict(districtCode, function (villages) {
                if (!isNull(villages)) {
                    populateSelectList(villages, controlVarId + "_cbxVillages");
                }
            });
    };

    this.fillHamlets = function () {
        $("#" + controlVarId + "_cbxHamlets").empty();
        var villageCode = $("#" + controlVarId + "_cbxVillages").val();

        if (!isNullOrEmpty(villageCode))
            RefDataDao.getHamletsByVillage(villageCode, function (hamlets) {
                if (!isNull(hamlets)) {
                    populateSelectList(hamlets, controlVarId + "_cbxHamlets");
                }
            });
    };

    this.saveAttributes = function () {
        if (!validateForm(true) || isNull(editedParcel)) {
            return;
        }

        if (!isNullOrEmpty($("#" + controlVarId + "_txtSurveyDate").val())) {
            editedParcel.attributes.surveyDate = dateFormat($("#" + controlVarId + "_txtSurveyDate").datepicker("getDate"), dateFormat.masks.isoDateTime);
        }

        if (!isNullOrEmpty($("#" + controlVarId + "_cbxLandTypes").val())) {
            editedParcel.attributes.landTypeCode = $("#" + controlVarId + "_cbxLandTypes").val();
        } else {
            editedParcel.attributes.landTypeCode = null;
        }

        if (!isNullOrEmpty($("#" + controlVarId + "_cbxHamlets").val())) {
            editedParcel.attributes.hamletCode = $("#" + controlVarId + "_cbxHamlets").val();
        } else {
            editedParcel.attributes.hamletCode = null;
        }

        if (!isNullOrEmpty($("#" + controlVarId + "_txtParcelAddress").val())) {
            editedParcel.attributes.address = $("#" + controlVarId + "_txtParcelAddress").val();
        } else {
            editedParcel.attributes.address = null;
        }

        if (!isNullOrEmpty($("#" + controlVarId + "_txtParcelComments").val())) {
            editedParcel.attributes.comment = $("#" + controlVarId + "_txtParcelComments").val();
        } else {
            editedParcel.attributes.comment = null;
        }
        $("#" + controlVarId + "_ParcelDialog").modal('hide');
    };

    this.saveParcels = function (onSave) {
        if (that.validateParcels(true)) {
            PropertyDao.saveParcels(that.getParcels(), function (list) {
                // Update features
                layerSelectedParcels.removeAllFeatures();
                parcels = list;

                if (list.length > 0) {
                    for (var i = 0; i < list.length; i++) {
                        var parcel = wkt.read(list[i].geom);
                        parcel.geometry.transform(sourceCrs, destCrs);
                        parcel.attributes = $.extend(true, {}, list[i]);
                        layerSelectedParcels.addFeatures(parcel);

                        // Update parcels to make sure it has same precision of coordinates
                        var clone = parcel.clone();
                        clone.geometry.transform(destCrs, sourceCrs);
                        parcels[i].geom = wkt.write(clone);
                    }
                    layerSelectedParcels.redraw();
                    map.zoomToExtent(layerSelectedParcels.getDataExtent());
                }
                if (isFunction(onSave)) {
                    onSave();
                }
            });
        }
    };

    this.hasChanges = function () {
        var mapParcels = that.getParcels();
        if (isNull(parcels)) {
            parcels = [];
        }
        if (parcels.length !== mapParcels.length) {
            return true;
        }
        for (var i = 0; i < parcels.length; i++) {
            if (JSON.stringify(parcels[i]) !== JSON.stringify(mapParcels[i])) {
                return true;
            }
        }
        return false;
    };

    this.validateParcels = function (showErrors) {
        if (layerSelectedParcels === null || layerSelectedParcels.features.length < 1) {
            alertErrorMessage($.i18n("err-parcel-no-parcels"));
            return false;
        }
        var valid = true;
        var count = 0;
        $.each(layerSelectedParcels.features, function (index, feature) {
            if (isParcel(feature)) {
                count += 1;
                if (isNullOrEmpty(feature.attributes.surveyDate) ||
                        isNullOrEmpty(feature.attributes.hamletCode)) {
                    if (showErrors) {
                        alertErrorMessage($.i18n("err-parcel-incomplete-attributes"));
                    }
                    valid = false;
                    return false;
                }
            }
        });
        if (valid) {
            if (count < 1) {
                if (showErrors) {
                    alertErrorMessage($.i18n("err-parcel-no-parcels"));
                }
                return false;
            }
            // Check application type
            if (count > 1) {
                if (showErrors) {
                    alertErrorMessage($.i18n("err-parcel-many-parcels"));
                }
                return false;
            }
        }
        return valid;
    };

    this.getParcels = function () {
        if (layerSelectedParcels === null || layerSelectedParcels.features.length < 1) {
            return [];
        }
        var parcelsToSave = [];
        $.each(layerSelectedParcels.features, function (index, feature) {
            if (isParcel(feature)) {
                var parcel = feature.clone();
                parcel.geometry.transform(destCrs, sourceCrs);
                parcel.attributes.geom = wkt.write(parcel);
                parcelsToSave.push(parcel.attributes);
            }
        });
        return parcelsToSave;
    };

    var isParcel = function (feature) {
        return typeof feature.attributes.uka !== 'undefined';
    };

    var validateForm = function (showErrors) {
        var errors = [];

        if (isNullOrEmpty($("#" + controlVarId + "_txtSurveyDate").val())) {
            errors.push($.i18n("err-parcel-survey-date-empty"));
        }
        if (isNullOrEmpty($("#" + controlVarId + "_cbxHamlets").val())) {
            errors.push($.i18n("err-parcel-hamlet-empty"));
        }

        if (errors.length > 0 && showErrors) {
            alertErrorMessages(errors);
        }

        return errors.length < 1;
    };

    // Turns off or on map editing
    var toggleMapEditing = function (enable) {
        if (enableMapEditing !== enable) {
            // Search for toolbar button
            for (var i = 0; i < mapToolbar.items.items.length; i++) {
                var tbButton = mapToolbar.items.items[i];
                if (tbButton.id === Controls.Map.TOOLBAR_BUTTON_IDS.EDIT_MAP) {
                    tbButton.toggle(enable);
                }
            }
        }
    };

    var addLayerOptions = function (layer, obj, isForServer) {
        if (isNull(layer) || isNull(layer.options) || layer.options.length < 1 || isNull(obj)) {
            return obj;
        }

        for (var i = 0; i < layer.options.length; i++) {
            if (String.empty(layer.options[i].name) !== "LEGEND_OPTIONS") {
                if ((layer.options[i].forServer && isForServer) || (!layer.options[i].forServer && !isForServer)) {
                    obj[layer.options[i].name] = layer.options[i].val;
                }
            }
        }
        return obj;
    };

    var addLegendOptions = function (layer) {
        if (isNull(layer) || isNull(layer.options) || layer.options.length < 1) {
            return "";
        }

        for (var i = 0; i < layer.options.length; i++) {
            if (String.empty(layer.options[i].name) === "LEGEND_OPTIONS") {
                return layer.options[i].val;
            }
        }
        return "";
    };

    // Public getters
    this.getMap = function () {
        return map;
    };
    this.getMapToolbar = function () {
        return mapToolbar;
    };
    this.getMapLegend = function () {
        return mapLegendTree;
    };
    this.getIsMapEditable = function () {
        return editable;
    };

    // Turn on/off feature selection button
    function snapClicked(item, pressed) {
        if (pressed) {
            // Enable button
            enableDisableToolbarButton(Controls.Map.TOOLBAR_BUTTON_IDS.SNAP_SELECT, true);
        } else {
            // Remove features for snapping and disable selection button
            if (layerSnappingFeatures) {
                //layerSnappingFeatures.removeAllFeatures();
            }
            enableDisableToolbarButton(Controls.Map.TOOLBAR_BUTTON_IDS.SNAP_SELECT, false);
        }
    }

    function toggleToolbarButton(buttonId, enable) {
        for (var i = 0; i < mapToolbar.items.items.length; i++) {
            var tbButton = mapToolbar.items.items[i];
            if (tbButton.id === buttonId) {
                tbButton.toggle(enable);
                break;
            }
        }
    }

    function enableDisableToolbarButton(buttonId, enable) {
        for (var i = 0; i < mapToolbar.items.items.length; i++) {
            var tbButton = mapToolbar.items.items[i];
            if (tbButton.id === buttonId) {
                var control = tbButton.baseAction.control;
                if (enable) {
                    tbButton.enable();
                } else {
                    control.deactivate();
                    tbButton.disable();
                }
                break;
            }
        }
    }

    // Maximize or minimize map control
    function maximizeMap(item, pressed) {
        var escContainerName = "#" + targetElementId;
        if (pressed) {
            $(escContainerName).addClass("fullScreen");
            mapPanelContainer.setHeight($(window).height());
            mapPanelContainer.setWidth($('body').innerWidth());
        } else {
            $(escContainerName).removeClass("fullScreen");
            mapPanelContainer.setHeight(mapHeight);
            mapPanelContainer.setWidth($(escContainerName).parent().width());
        }
    }

    /** Renders map into provided html container */
    var renderMap = function () {
        setTimeout(function () {
            if (!isRendered) {
                mapPanelContainer.render(targetElementId);
                map.zoomToExtent(initialZoomBounds);
                mapPanelContainer.setWidth($("#" + targetElementId).parent().width());
                map.addControl(new OpenLayers.Control.MousePosition({div: document.getElementById(controlVarId + "_lblMapMousePosition")}));
                map.addControl(new Controls.Map.Control.ScaleBar({div: document.getElementById(controlVarId + "_lblScaleBar")}));
                isRendered = true;
            }
        }, 0);
    };

    // Subscribe to map resize event to adjust map width/height
    $(window).resize(function () {
        var escContainerName = "#" + targetElementId;
        if ($(escContainerName).hasClass("fullScreen")) {
            mapPanelContainer.setHeight($(window).height());
            mapPanelContainer.setWidth($('body').innerWidth());
        } else {
            mapPanelContainer.setWidth($(escContainerName).parent().width());
        }
    });

    // Toolbar legend and layer handlers

    // When map legend node gets selected, underlying layer will be recorded as selected
    function nodeSelectionHandler(node, e, forceSelect) {
        if (forceSelect) {
            node = mapLegendTree.getNodeById(node.id);
            mapLegendTree.getSelectionModel().select(node);
        }
        // Clean selected node editing css class
        if (selectedNode === null || selectedNode.id !== node.id) {
            selectedNode = node;
            if (enableMapEditing) {
                customizeMapToolbar();
            }
        }
        return true;
    }

    // Turns on and off map editing
    function onMapEditToggle(item, pressed) {
        enableMapEditing = pressed;
        customizeMapToolbar();
    }

    function onImportPointsClick() {
        if ($("#" + controlVarId + "_ImportPointsDialog").length === 0) {
            var html = '<div class="modal fade" id="' + controlVarId + '_ImportPointsDialog" tabindex="-1" role="dialog" aria-hidden="true"> \
                        <div class="modal-dialog" style="width:500px;"> \
                            <div class="modal-content"> \
                                <div class="modal-header"> \
                                    <button type="button" class="close" data-dismiss="modal"><span aria-hidden="true">&times;</span><span class="sr-only" data-i18n="gen-close"></span></button> \
                                    <h4 class="modal-title" data-i18n="gen-import"></h4> \
                                </div> \
                                <div class="modal-body" style="padding: 0px 5px 0px 5px;"> \
                                    <div class="content"> \
                                        <div class="row"> \
                                            <div class="col-md-6"> \
                                                <label data-i18n="map-control-points-type"></label> \
                                                <select id="' + controlVarId + '_cbxPointsType" class="form-control"></select> \
                                            </div> \
                                            <div class="col-md-4"> \
                                                <label data-i18n="map-control-crs"></label> \
                                                <select id="' + controlVarId + '_cbxCrs" class="form-control"></select> \
                                            </div> \
                                        </div> \
                                        <div class="LineSpace"></div> \
                                        <label data-i18n="map-control-points"></label> \
                                        <textarea id="' + controlVarId + '_txtPoints" rows="5" class="form-control"></textarea> \
                                    </div> \
                                </div> \
                                <div class="modal-footer" style="margin-top: 0px;padding: 15px 20px 15px 20px;"> \
                                    <button type="button" class="btn btn-default" data-dismiss="modal" data-i18n="gen-close"></button> \
                                    <button type="button" id="' + controlVarId + '_btnImportPoints" class="btn btn-primary" onclick="' + controlVarId + '.importPoints()" data-i18n="gen-import"></button> \
                                </div> \
                            </div> \
                        </div> \
                    </div>';

            $('#' + targetElementId).append(html);
            // Localize
            $('#' + targetElementId).i18n();
            // Populate lists
            $("#" + controlVarId + "_cbxPointsType").append($("<option />").val("wkt").text($.i18n("map-control-wkt")));
            $("#" + controlVarId + "_cbxPointsType").append($("<option />").val(",").text($.i18n("map-control-comma")));
            $("#" + controlVarId + "_cbxPointsType").append($("<option />").val(";").text($.i18n("map-control-semicolon")));

            $("#" + controlVarId + "_cbxCrs").append($("<option />").val(sourceCrs).text(sourceCrs));
            if (!isNullOrEmpty(printCrs) && printCrs !== sourceCrs) {
                $("#" + controlVarId + "_cbxCrs").append($("<option />").val(printCrs).text(printCrs));
            }
        }

        $("#" + controlVarId + "_ImportPointsDialog").modal('show');
    }

    this.importPoints = function () {
        var coords = $("#" + controlVarId + "_txtPoints").val().trim();
        var pointsType = $("#" + controlVarId + "_cbxPointsType").val();
        var crsCode = $("#" + controlVarId + "_cbxCrs").val();

        if (isNullOrEmpty(coords)) {
            alertErrorMessage($.i18n("err-parcel-no-coords"));
            return;
        }

        try {
            var parcel;
            if (pointsType === "wkt") {
                if (coords.toLowerCase().indexOf("polygon") < 0) {
                    alertErrorMessage($.i18n("err-parcel-not-polygon"));
                    return;
                }
            } else {
                // Check number of oordinates and complete polygon if needed
                var arrayCoords = coords.split(pointsType);

                if (arrayCoords.length > 2) {
                    if (arrayCoords[0].replace(/ /g, "") !== arrayCoords[arrayCoords.length - 1].replace(/ /g, "")) {
                        // Add last point to complete polygon
                        arrayCoords.push(arrayCoords[0].trim());
                        arrayCoords = arrayCoords + pointsType + " " + arrayCoords[0].trim();
                    }
                }

                if (arrayCoords.length < 4) {
                    alertErrorMessage($.i18n("err-parcel-3points-min"));
                    return;
                }

                if (pointsType === ",") {
                    coords = "Polygon((" + coords + "))";
                } else {
                    coords = "Polygon((" + coords.replace(/;/g, ",") + "))";
                }
            }

            parcel = wkt.read(coords);
            parcel.geometry.transform(crsCode, destCrs);
            layerSelectedParcels.addFeatures(parcel);

            // Zoom to boundary
            map.zoomToExtent(parcel.geometry.getBounds(), closest=true);

            // Show attributes popup
            $("#" + controlVarId + "_ImportPointsDialog").modal('hide');
            editParcelAttributes(parcel);

        } catch (ex) {
            alertErrorMessage(ex);
        }
    };

    // Layer property change handler
    function handleLayerChange(evt) {
        if (evt.property === 'visibility') {
            if (selectedNode !== null && selectedNode.layer.id === evt.layer.id) {
                if (enableMapEditing) {
                    customizeMapToolbar();
                }
            }
        }
    }

    function handleAddLayer(evt) {
        // Move snapping layer on top
        if (map && layerSnappingFeatures) {
            if (evt.layer.name !== layerSnappingFeatures.name) {
                // Check snapping layer in the list
                var found = false;
                for (var i = 0; i < map.layers.length; i++) {
                    if (map.layers[i].name === layerSnappingFeatures.name) {
                        // Move on top
                        map.setLayerIndex(layerSnappingFeatures, map.getLayerIndex(evt.layer));
                        found = true;
                    }
                }
                if (!found) {
                    // Add snapping layer
                    map.addLayer(layerSnappingFeatures);
                }
            }
        }
    }

    // Customizes map toolbar
    function customizeMapToolbar() {
        if (!enableMapEditing || selectedNode === null || !selectedNode.layer.isEditable || !selectedNode.layer.visibility) {
            disableMapEditing();
            return;
        }

        removeLayerEditingIcon();
        var selectedLayer = selectedNode.layer;

        if (typeof selectedNode.ui.textNode !== 'undefined') {
            $(selectedNode.ui.textNode).parent().parent().addClass('editingNode');
        }

        // Enable toolbar items
        for (var i = 0; i < mapToolbar.items.items.length; i++) {
            var tbButton = mapToolbar.items.items[i];
            if (tbButton.editingTool) {

                // Enable shape editing tool
                var allowDrawing = true;
                if (typeof selectedLayer.allowMultipleFeatures !== 'undefined') {
                    if (!selectedLayer.allowMultipleFeatures && selectedLayer.features.length > 0) {
                        allowDrawing = false;
                    }
                }

                if (tbButton.id === Controls.Map.TOOLBAR_BUTTON_IDS.SNAP) {
                    var control = tbButton.baseAction.control;
                    var active = control.active;

                    if (active)
                        control.deactivate();

                    control.setLayer(selectedLayer);

                    if (active)
                        control.activate();
                    else
                        tbButton.enable();

                } else if (tbButton.id === Controls.Map.TOOLBAR_BUTTON_IDS.SNAP_SELECT) {
                    // Do nothing
                } else if (tbButton.id === Controls.Map.TOOLBAR_BUTTON_IDS.IMPORT_POINTS) {
                    tbButton.enable();
                } else if (tbButton.id === Controls.Map.TOOLBAR_BUTTON_IDS.DELETE_FEATURE) {
                    var control = tbButton.baseAction.control;
                    var active = control.active;

                    if (active)
                        control.deactivate();

                    control.setLayer(selectedLayer);

                    if (active)
                        control.activate();
                    else
                        tbButton.enable();

                } else if (tbButton.id === Controls.Map.TOOLBAR_BUTTON_IDS.EDIT_SHAPE) {
                    var control = tbButton.baseAction.control;
                    var active = control.active;

                    if (active)
                        control.deactivate();

                    control.layer = selectedLayer;
                    control.virtualStyle = selectedLayer.virtualNodeStyle;

                    if (active)
                        control.activate();
                    else
                        tbButton.enable();

                } else if (tbButton.id === Controls.Map.TOOLBAR_BUTTON_IDS.EDIT_FEATURE && typeof selectedLayer.editFeatureFunc !== 'undefined') {
                    var control = tbButton.baseAction.control;
                    var active = control.active;

                    if (active)
                        control.deactivate();

                    control.setLayer(selectedLayer);

                    if (active)
                        control.activate();
                    else
                        tbButton.enable();
                } else if (((tbButton.id === Controls.Map.TOOLBAR_BUTTON_IDS.DRAW_POLYGON && selectedLayer.allowPolygon) ||
                        (tbButton.id === Controls.Map.TOOLBAR_BUTTON_IDS.DRAW_LINE && selectedLayer.allowLine) ||
                        (tbButton.id === Controls.Map.TOOLBAR_BUTTON_IDS.DRAW_POINT && selectedLayer.allowPoint))
                        && allowDrawing) {

                    var control = tbButton.baseAction.control;
                    var active = control.active;

                    if (active)
                        control.deactivate();

                    control.layer = selectedLayer;
                    control.handler.style = selectedLayer.virtualNodeStyle;

                    if (active)
                        control.activate();
                    else
                        tbButton.enable();

                } else {
                    tbButton.toggle(false);
                    tbButton.disable();
                }
            }
        }
    }

    // Calls feature editing functions, related to the layer it belongs to
    function featureAdded(feature) {
        customizeMapToolbar();
        if (feature !== null && typeof feature.layer.editFeatureFunc !== 'undefined') {
            feature.layer.editFeatureFunc(feature);
        }
    }

    // Removes editing icon from selected layer
    function removeLayerEditingIcon() {
        if (typeof mapLegendTree.root.childNodes !== 'undefined') {
            for (var i = 0; i < mapLegendTree.root.childNodes.length; i++) {
                if (typeof mapLegendTree.root.childNodes[i].ui.textNode !== 'undefined') {
                    $(mapLegendTree.root.childNodes[i].ui.textNode).parent().parent().removeClass('editingNode');
                }
            }
        }
    }

    // Disable all editing tools
    function disableMapEditing() {
        removeLayerEditingIcon();
        for (var i = 0; i < mapToolbar.items.items.length; i++) {
            if (mapToolbar.items.items[i].editingTool) {
                mapToolbar.items.items[i].toggle(false);
                mapToolbar.items.items[i].disable();
            }
        }
    }

    // Claim information tool
    var mapWaitContent = "<div id='mapWaitContent' class='mapWaitDiv'>" + $.i18n("map-control-loading") + "</div>";
    var mapNoResutlsContent = "<div id='mapNoResutlsContent' class='mapNoResultsDiv'>" + $.i18n("map-control-claim-not-found") + "</div>";
    var mapClaimInfoContent = "<div id='" + controlVarId + "_mapClaimInfoContent' class='mapClaimInfoDiv'>" +
            "<div class='line'>" +
            "<b><u><span id='" + controlVarId + "_lblUka'></span></u></b>" +
            "</div>" +
            "<div class='line'>" + $.i18n("parcel-survey-date") +
            "<br /><b><span id='" + controlVarId + "_lblSurveyDate'></span></b> " +
            "</div>" +
            "<div class='line'>" + $.i18n("parcel-location") +
            " <br /><b><span id='" + controlVarId + "_lblLocation'></span></b>" +
            "</div>" +
            "<div class='line'>" + $.i18n("map-control-status") +
            " <br /><b><span id='" + controlVarId + "_lblStatus'></span></b>" +
            "</div>" +
            "<div class='line'>" + $.i18n("map-control-area") +
            " <br /><b><span id='" + controlVarId + "_lblArea'></span></b>" +
            "</div>" +
            "<div class='line'>" + $.i18n("prop-props") +
            " <br /><b><span id='" + controlVarId + "_lblCcros'></span></b>" +
            "</div>" +
            "</div>";

    var claimInfoPopup = null;
    var xhr;

    function handleClaimInfoClick(evt) {
        if (typeof xhr !== 'undefined') {
            xhr.abort();
        }

        var lonlat = map.getLonLatFromViewPortPx(evt.xy);
        var coords = map.getLonLatFromViewPortPx(evt.xy).transform(destCrs, sourceCrs);

        if (claimInfoPopup === null) {
            claimInfoPopup = new OpenLayers.Popup.FramedCloud($.i18n("map-control-claim-info"),
                    lonlat,
                    new OpenLayers.Size(220, 220),
                    mapWaitContent,
                    null, true, null);
            claimInfoPopup.panMapIfOutOfView = true;
            map.addPopup(claimInfoPopup);
        } else {
            claimInfoPopup.lonlat = lonlat;
            claimInfoPopup.setContentHTML(mapWaitContent);
            claimInfoPopup.show();
        }

        SearchDao.searchParcelByPoint(coords.lon, coords.lat, function (result) {
            if (isNull(result)) {
                claimInfoPopup.setContentHTML(mapNoResutlsContent);
            } else {
                populateFeatureInfo(result);
            }
        });
    }

    function populateFeatureInfo(response) {
        try {
            if (isNullOrEmpty(response)) {
                claimInfoPopup.setContentHTML(mapNoResutlsContent);
            } else {
                claimInfoPopup.setContentHTML(mapClaimInfoContent);
                var feature = wkt.read(response.geom);
                feature.geometry.transform(sourceCrs, destCrs);

                if (!isNull(response.surveyDate)) {
                    $("#" + controlVarId + "_lblSurveyDate").text(dateFormat(response.surveyDate));
                } else {
                    $("#" + controlVarId + "_lblSurveyDate").text("");
                }
                $("#" + controlVarId + "_lblUka").text('#' + response.uka);
                $("#" + controlVarId + "_lblLocation").text(response.parcelLocation);
                $("#" + controlVarId + "_lblStatus").text(response.statusName);
                $("#" + controlVarId + "_lblArea").html(calculateArea(feature) + " m<sup>2</sup>");

                var ccros = "";
                if (!isNullOrEmpty(response.propCodes) && response.propCodes.length > 0) {
                    for (var i = 0; i < response.propCodes.length; i++) {
                        if (ccros !== "") {
                            ccros += ", ";
                        }
                        ccros += String.format("<a href='{0}' target='_blank'>{1}</a>",
                                String.format(URLS.VIEW_PROPERTY, response.propCodes[i].id),
                                response.propCodes[i].propNumber);
                    }
                }
                $("#" + controlVarId + "_lblCcros").html(ccros);
            }
        } catch (ex) {
            claimInfoPopup.hide();
            alert(ex);
        }
    }

    function handleSelectSnapFeatureClick(evt) {
        if (typeof xhr !== 'undefined') {
            xhr.abort();
        }

        var coords = map.getLonLatFromViewPortPx(evt.xy).transform(destCrs, sourceCrs);
        SearchDao.searchParcelByPoint(coords.lon, coords.lat, function (response) {
            try {
                if (!isNull(response)) {
                    if (layerSnappingFeatures) {
                        // Check if claim alredy in the list
                        var featureExists = false;
                        var featuresToRemove = [];
                        for (var i = 0; i < layerSnappingFeatures.features.length; i++) {
                            if (layerSnappingFeatures.features[i].attributes.id === response.id) {
                                featureExists = true;
                                // Remove feature (deselect)
                                featuresToRemove.push(layerSnappingFeatures.features[i]);
                            }
                        }
                        if (featuresToRemove.length > 0) {
                            layerSnappingFeatures.removeFeatures(featuresToRemove);
                        }
                    }

                    if (!featureExists) {
                        // Check it's not the current claim
                        var claimLayer = map.getLayer(Controls.Map.LAYER_IDS.CURRENT_CLAIM);
                        if (claimLayer) {
                            for (var i = 0; i < claimLayer.features.length; i++) {
                                if (claimLayer.features[i].attributes.id === response.id) {
                                    featureExists = true;
                                    break;
                                }
                            }
                        }

                        // Add feature to the snapping layer
                        if (!featureExists) {
                            var featureToAdd = new OpenLayers.Format.WKT().read(response.geom);
                            featureToAdd.attributes = response;
                            featureToAdd.geometry.transform(sourceCrs, destCrs);
                            layerSnappingFeatures.addFeatures([featureToAdd]);

                            var vertices = featureToAdd.geometry.getVertices();
                            if (!isNull(vertices) && vertices.length > 0) {
                                var verticesFeatures = [];
                                for (var i = 0; i < vertices.length; i++) {
                                    verticesFeatures.push(new OpenLayers.Feature.Vector(
                                            new OpenLayers.Geometry.Point(vertices[i].x, vertices[i].y),
                                            {id: featureToAdd.attributes.id, uka: featureToAdd.attributes.uka}));
                                }
                                layerSnappingFeatures.addFeatures(verticesFeatures);
                            }
                        }
                    }
                }
            } catch (ex) {
                alert(ex);
            }
        });
    }
};

Controls.Map.Layer = {};
Controls.Map.Control = {};

// Map toolbar buttons ids
Controls.Map.TOOLBAR_BUTTON_IDS = {
    ZOOM_TO_EXTENT: "btnZoomToExtent",
    ZOOM_IN: "btnZoomIn",
    ZOOM_OUT: "btnZoomOut",
    PAN: "btnPan",
    CLAIM_INFO: "btnClaimInfo",
    EDIT_MAP: "btnEditMap",
    DRAW_POLYGON: "btnDrawPolygon",
    DRAW_LINE: "btnDrawLine",
    DRAW_POINT: "btnDrawPoint",
    EDIT_SHAPE: "btnEditShape",
    ADD_NODE: "btnAddNode",
    REMOVE_NODE: "btnRemoveNode",
    DELETE_FEATURE: "btnDeleteFeature",
    EDIT_FEATURE: "btnEditFeature",
    SNAP: "btnSnapping",
    SNAP_SELECT: "btnSelectForSnapping",
    MAXIMIZE_MAP: "btnMaximizeMap",
    IMPORT_POINTS: "btnImportPoints"
};

// Map layers ids
Controls.Map.LAYER_IDS = {
    COMMUNITY_AREA: "layerCommunityArea",
    GOOGLE_EARTH: "layerGoogleEarth",
    GOOGLE_MAP: "layerGoogleMap",
    CURRENT_CLAIM: "layerCurrentClaim",
    CLAIM_ADDITIONAL_LOCATIONS: "layerClaimAdditionalLocations",
    SNAPPING_FEATURES: "layerSnappingFeatures"
};

// Extend OpenLayers objects
Controls.Map.Control.ScaleBar = OpenLayers.Class(OpenLayers.Control.ScaleBar, {
    styleValue: function (selector, key) {
        var value = 0;
        if (this.limitedStyle) {
            value = this.appliedStyles[selector][key];
        } else {
            selector = "." + this.displayClass + selector;
            rules:
                    for (var i = document.styleSheets.length - 1; i >= 0; --i) {
                var sheet = document.styleSheets[i];
                if (!sheet.disabled) {
                    var allRules;
                    try {
                        if (typeof (sheet.cssRules) == 'undefined') {
                            if (typeof (sheet.rules) == 'undefined') {
                                // can't get rules, keep looking
                                continue;
                            } else {
                                allRules = sheet.rules;
                            }
                        } else {
                            allRules = sheet.cssRules;
                        }
                    } catch (err) {
                        continue;
                    }
                    if (allRules && allRules !== null) {
                        for (var ruleIndex = 0; ruleIndex < allRules.length; ++ruleIndex) {
                            var rule = allRules[ruleIndex];
                            if (rule.selectorText &&
                                    (rule.selectorText.toLowerCase() == selector.toLowerCase())) {
                                if (rule.style[key] != '') {
                                    value = parseInt(rule.style[key]);
                                    break rules;
                                }
                            }
                        }
                    }
                }
            }
        }
        // if the key was not found, the equivalent value is zero
        return value ? value : 0;
    }
});

Controls.Map.Layer.VectorLayer = function (id, name, params) {
    OpenLayers.Layer.Vector.call(this, name, params);
    if (id) {
        this.id = id;
    }
    this.isEditable = true;
    this.allowPolygon = true;
    this.allowPoint = false;
    this.allowLine = false;
    this.allowMultipleFeatures = false;
    this.virtualNodeStyle = "";
    this.editFeatureFunc;

    if (params.hasOwnProperty('virtualNodeStyle'))
        this.virtualNodeStyle = params.virtualNodeStyle;
    if (params.hasOwnProperty('isEditable'))
        this.isEditable = params.isEditable;
    if (params.hasOwnProperty('allowPolygon'))
        this.allowPolygon = params.allowPolygon;
    if (params.hasOwnProperty('allowPoint'))
        this.allowPoint = params.allowPoint;
    if (params.hasOwnProperty('allowLine'))
        this.allowLine = params.allowLine;
    if (params.hasOwnProperty('allowMultipleFeatures'))
        this.allowMultipleFeatures = params.allowMultipleFeatures;
    if (params.hasOwnProperty('editFeatureFunc'))
        this.editFeatureFunc = params.editFeatureFunc;
    return this;
};
Controls.Map.Layer.VectorLayer.prototype = createObject(OpenLayers.Layer.Vector.prototype);
Controls.Map.Layer.VectorLayer.prototype.constructor = Controls.Map.Layer.VectorLayer;

// Extend drawing control
Controls.Map.Control.DrawFeature = OpenLayers.Class(OpenLayers.Control.DrawFeature, {
    handlers: null,
    initialize: function (layer, handler, options) {
        OpenLayers.Control.DrawFeature.prototype.initialize.apply(this, [layer, handler, options]);
        // configure the keyboard handler
        var keyboardOptions = {
            keydown: this.handleKeypress
        };
        this.handlers = {
            keyboard: new OpenLayers.Handler.Keyboard(this, keyboardOptions)
        };
    },
    handleKeypress: function (evt) {
        var code = evt.keyCode;
        // ESCAPE pressed. Remove feature from map
        if (code === 27) {
            this.cancel();
        }
        // DELETE pressed. Remove third last vertix
        if (code === 46) {
            this.undo();
        }
        return true;
    },
    activate: function () {
        return this.handlers.keyboard.activate() &&
                OpenLayers.Control.DrawFeature.prototype.activate.apply(this, arguments);
    },
    deactivate: function () {
        var deactivated = false;
        // the return from the controls is unimportant in this case
        if (OpenLayers.Control.DrawFeature.prototype.deactivate.apply(this, arguments)) {
            this.handlers.keyboard.deactivate();
            deactivated = true;
        }
        return deactivated;
    },
    CLASS_NAME: "Controls.DrawFeature"
});

Controls.Map.Styles = {
    styleMapCommunityLayer: new OpenLayers.StyleMap({'default': {
            strokeColor: "#F5856F",
            strokeWidth: 3,
            fillOpacity: 0
        }}),
    styleLocationsNode: {
        pointRadius: 5,
        graphicName: "circle",
        fillColor: "white",
        fillOpacity: 0.5,
        strokeWidth: 2,
        strokeOpacity: 0.3,
        strokeColor: "#E96EFF"
    },
    styleMapLocations: new OpenLayers.StyleMap({
        "default": new OpenLayers.Style({
            label: "${getLabel}",
            fontSize: "12px",
            fontFamily: "Arial",
            labelOutlineColor: "white",
            labelOutlineWidth: 3
        }, {
            context: {getLabel: function (feature) {
                    if (typeof feature.attributes.description !== 'undefined') {
                        return feature.attributes.description;
                    } else {
                        return "";
                    }
                }},
            rules: [
                new OpenLayers.Rule({
                    symbolizer: {
                        "Point": {
                            pointRadius: 5,
                            graphicName: "circle",
                            fillColor: "white",
                            fillOpacity: 0.7,
                            labelYOffset: -10,
                            strokeWidth: 2,
                            strokeColor: "#E96EFF"
                        },
                        "Line": {
                            strokeWidth: 2,
                            strokeColor: "#E96EFF",
                            labelYOffset: 10,
                            labelAlign: "l"
                        },
                        "Polygon": {
                            fillColor: "#ED87FF",
                            fillOpacity: 0,
                            strokeColor: "#E96EFF",
                            strokeWidth: 2
                        }
                    }
                })
            ]
        }),
        "select": new OpenLayers.Style(null, {
            rules: [
                new OpenLayers.Rule({
                    symbolizer: {
                        "Point": {
                            pointRadius: 7,
                            labelYOffset: -10,
                            graphicName: "circle",
                            fillColor: "white",
                            fillOpacity: 1,
                            strokeWidth: 2,
                            strokeColor: "#E96EFF"
                        },
                        "Line": {
                            strokeWidth: 3,
                            strokeColor: "#E96EFF",
                            labelYOffset: 10,
                            labelAlign: "l"
                        },
                        "Polygon": {
                            fillColor: "#E96EFF",
                            fillOpacity: 0.3,
                            strokeColor: "#E96EFF",
                            strokeWidth: 2
                        }
                    }
                })
            ]
        }),
        "temporary": new OpenLayers.Style(null, {
            rules: [
                new OpenLayers.Rule({
                    symbolizer: {
                        "Point": {
                            pointRadius: 7,
                            labelYOffset: -10,
                            graphicName: "circle",
                            fillColor: "white",
                            fillOpacity: 1,
                            strokeWidth: 2,
                            strokeColor: "#E96EFF"
                        },
                        "Line": {
                            strokeWidth: 2,
                            strokeColor: "#E96EFF",
                            labelYOffset: 10,
                            labelAlign: "l"
                        },
                        "Polygon": {
                            fillColor: "#E96EFF",
                            fillOpacity: 0.3,
                            strokeColor: "#E96EFF",
                            strokeWidth: 2
                        }
                    }
                })
            ]
        })
    }),
    styleClaimNode: {
        pointRadius: 5,
        graphicName: "circle",
        fillColor: "white",
        fillOpacity: 0.5,
        strokeWidth: 2,
        strokeColor: "#00AAFF",
        strokeOpacity: 0.3
    },
    styleMapClaim: new OpenLayers.StyleMap({
        "default": new OpenLayers.Style({
            label: "${getLabel}",
            fontSize: "12px",
            fontFamily: "Arial",
            labelOutlineColor: "white",
            labelOutlineWidth: 3
        }, {
            context: {getLabel: function (feature) {
                    if (!isNull(feature.attributes) && !isNullOrEmpty(feature.attributes.uka)) {
                        return "\n\n(" + calculateArea(feature) + " m2)";
                    } else {
                        if (typeof feature.geometry.x === 'undefined') {
                            return "\n\n(" + calculateArea(feature) + " m2)";
                        } else {
                            return "";
                        }
                    }
                }},
            rules: [
                new OpenLayers.Rule({
                    symbolizer: {
                        "Point": {
                            pointRadius: 5,
                            labelYOffset: -10,
                            graphicName: "circle",
                            fillColor: "white",
                            fillOpacity: 0.7,
                            strokeWidth: 2,
                            strokeColor: "#00AAFF"
                        },
                        "Line": {
                            strokeWidth: 2,
                            strokeColor: "#00AAFF",
                            labelYOffset: 10,
                            labelAlign: "l"
                        },
                        "Polygon": {
                            fillColor: "#0000ff",
                            fillOpacity: 0,
                            strokeColor: "#33BBFF",
                            strokeWidth: 2
                        }
                    }
                })
            ]
        }),
        "select": new OpenLayers.Style(null, {
            rules: [
                new OpenLayers.Rule({
                    symbolizer: {
                        "Point": {
                            pointRadius: 7,
                            labelYOffset: -10,
                            graphicName: "circle",
                            fillColor: "white",
                            fillOpacity: 1,
                            strokeWidth: 2,
                            strokeColor: "#00AAFF"
                        },
                        "Line": {
                            strokeWidth: 3,
                            strokeColor: "#00AAFF",
                            labelYOffset: 10,
                            labelAlign: "l"
                        },
                        "Polygon": {
                            fillColor: "#33BBFF",
                            fillOpacity: 0.3,
                            strokeColor: "#00AAFF",
                            strokeWidth: 2
                        }
                    }
                })
            ]
        }),
        "temporary": new OpenLayers.Style(null, {
            rules: [
                new OpenLayers.Rule({
                    symbolizer: {
                        "Point": {
                            pointRadius: 7,
                            labelYOffset: -10,
                            graphicName: "circle",
                            fillColor: "white",
                            fillOpacity: 1,
                            strokeWidth: 2,
                            strokeColor: "#00AAFF"
                        },
                        "Line": {
                            strokeWidth: 2,
                            strokeColor: "#00AAFF",
                            labelYOffset: 10,
                            labelAlign: "l"
                        },
                        "Polygon": {
                            fillColor: "#33BBFF",
                            fillOpacity: 0.3,
                            strokeColor: "#00AAFF",
                            strokeWidth: 2
                        }
                    }
                })
            ]
        })
    }),
    styleSnappingClaim: new OpenLayers.StyleMap({
        "default": new OpenLayers.Style({
            strokeColor: "#FF7300"
        }, {
            rules: [
                new OpenLayers.Rule({
                    symbolizer: {
                        "Polygon": {
                            fillColor: "#0000ff",
                            fillOpacity: 0,
                            strokeColor: "#FF7300",
                            strokeWidth: 2
                        },
                        "Point": {
                            pointRadius: 3,
                            graphicName: "circle",
                            fillColor: "white",
                            fillOpacity: 0.7,
                            strokeWidth: 1,
                            strokeColor: "#FF7300"
                        }
                    }
                })
            ]
        })
    })
};

function calculateArea(feature) {
    var area = feature.geometry.getArea();
    area = Math.round(area);
    return area;
}