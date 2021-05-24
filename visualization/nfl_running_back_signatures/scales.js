'use strict';

var svgWidth = 800;
var svgHeight = 400;

// TODO: Make minWidth 0?
var minWidth = 1;
var maxWidth = svgHeight / 4;

var colorSchemes = {
  buckets: {
    domain: [0.25, 1.75],
    range: [
      "#405A7C",
      "#7092C0",
      "#BDD9FF",
      "#FFA39E",
      "#F02C21",
      "#B80E05"
    ]
  },
  custom2: {
    domain: [0.55, 1.45],
    range: [
      "#B03A2E",
      "#CB4335",
      "#EC7063",
      "#F5B7B1",
      "#F9E79F",
      "#ABEBC6",
      "#58D68D",
      "#28B463",
      "#1D8348"
    ]
  },
  custom: {
    domain: [0.6, 1.4],
    range: [
      "#21618C",
      "#2874A6",
      "#2E86C1",
      "#3498DB",
      "#5DADE2",
      "#85C1E9",
      "#A9CCE3",
      "#E8DAEF",
      "#EBDEF0",
      "#F5B7B1",
      "#D98880",
      "#CD6155",
      "#C0392B",
      "#A93226",
      "#922B21",
      "#7B241C"
    ]
  }
};

// Scale mapping number of carries to width range at a given x.
var sigWidth = d3.scaleLinear()
  .domain([0, 300])
  .range([minWidth, maxWidth]);

// Scale mapping distance from endzone to SVG width.
var sigDistance = d3.scaleLinear()
  .domain([0, 100])
  .range([0, svgWidth]);

// Scale mapping YPC at given x to SVG height.
var sigHeight = d3.scaleLinear()
  .domain([0, 10])
  .range([svgHeight, 0]);

// Area generator for area below generated path (area width determined by sigWidth).
var areaAbove = d3.area()
  .x(function (d) { return sigDistance(d.x); })
  .y0(function (d) { return sigHeight(d.y) - sigWidth(d.w); })
  .y1(function (d) { return Math.ceil(sigHeight(d.y)); })
  .curve(d3.curveBasis);

// Area generator for area above generated path (area width determined by sigWidth).
var areaBelow = d3.area()
  .x(function (d) { return sigDistance(d.x); })
  .y0(function (d) { return sigHeight(d.y) + sigWidth(d.w); })
  .y1(function (d) { return Math.floor(sigHeight(d.y)); })
  .curve(d3.curveBasis);

// Color scale used for filling in signatures.
var colorScale = d3.scaleQuantize()
  .domain(colorSchemes.custom2.domain)
  .range(colorSchemes.custom2.range);

// To view guiding line.
var line = d3.line()
  .x(function(d) { return sigDistance(d.x); })
  .y(function(d) { return sigHeight(d.y); })
  .curve(d3.curveBasis);
  
