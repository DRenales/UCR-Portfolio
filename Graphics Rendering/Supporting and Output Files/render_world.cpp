#include "render_world.h"
#include "flat_shader.h"
#include "object.h"
#include "light.h"
#include "ray.h"

extern bool disable_hierarchy;

Render_World::Render_World()
    :background_shader(0),ambient_intensity(0),enable_shadows(true),
    recursion_depth_limit(3)
{}

Render_World::~Render_World()
{
    delete background_shader;
    for(size_t i=0;i<objects.size();i++) delete objects[i];
    for(size_t i=0;i<lights.size();i++) delete lights[i];
}

// Find and return the Hit structure for the closest intersection.  Be careful
// to ensure that hit.dist>=small_t.
Hit Render_World::Closest_Intersection(const Ray& ray)
{
    //TODO;
    /*
      Set min_t to extremely large value
      For each object* in objects:
        use objects->Intersect to get the hit with object
        If hit is the closest so far and larger than small_t
          store the hit as the closest hit
    */

    double min_t = std::numeric_limits<double>::max();
    Hit closest = {NULL, 0.0, 0};

    for(auto item : objects)
    {
      Hit hit = item->Intersection(ray, item->number_parts);
      //if(debug_pixel && )
      if (hit.dist >= small_t && hit.dist < min_t)
      {
         min_t = hit.dist;
         closest = hit;
      }
    }
    return closest;
}

// set up the initial view ray and call
void Render_World::Render_Pixel(const ivec2& pixel_index)
{
    //TODO; // set up the initial view ray here
    /*
      end_point = camera position
      direction is unit vector that takes from camera position to world position of pixel
    */

    Ray ray(this->camera.position, (this->camera.World_Position(pixel_index) - camera.position).normalized());
    vec3 color = Cast_Ray(ray,1);
    camera.Set_Pixel(pixel_index,Pixel_Color(color));
}

void Render_World::Render()
{
    if(!disable_hierarchy)
        Initialize_Hierarchy();

    for(int j=0;j<camera.number_pixels[1];j++)
        for(int i=0;i<camera.number_pixels[0];i++)
            Render_Pixel(ivec2(i,j));
}

// cast ray and return the color of the closest intersected surface point,
// or the background color if there is no object intersection
vec3 Render_World::Cast_Ray(const Ray& ray,int recursion_depth)
{
    //TODO; // determine the color here
    /*
      Get the closest hit with an object using Closest_Intersection
      If there is an intersection:
        Set color using the object Shade_Surface function which calculates and returns the color of the ray/object intersection Point
      Else:
        Use background_shader of the render_world class. *flat_shader therefore any vec3 parameters will do
    */


    vec3 color,
          temp;
    Hit closest = Closest_Intersection(ray);

    if(recursion_depth > this->recursion_depth_limit)
      return this->background_shader->Shade_Surface(ray, temp, temp, 1);

    if(closest.object != NULL)
    {
    //  std::cout << closest.dist << std::endl;
      color = closest.object->material_shader->Shade_Surface(ray, ray.Point(closest.dist), closest.object->Normal(ray.Point(closest.dist), closest.part), recursion_depth);
    }
    else
      color = this->background_shader->Shade_Surface(ray, color, color, 0);

    return color;

    /*
    Hit closest = Closest_Intersection(ray);
    if(debug_pixel && closest.object == NULL) std::cout << "No intersection" << std::endl;
    return closest.object ? closest.object->material_shader->Shade_Surface(ray, ray.Point(closest.dist), closest.object->Normal(ray.Point(closest.dist), closest.part), recursion_depth)
                                                       : this->background_shader->Shade_Surface(ray, this->ambient_color, this->ambient_color, 0);
    */
}

void Render_World::Initialize_Hierarchy()
{
    //TODO; // Fill in hierarchy.entries; there should be one entry for
    // each part of each object.
    // for(auto item : this->objects)
    // {
    //   Entry temp;
    //   temp.obj = item;
    //   hierarchy.entries.push_back(temp);
    // }

    for(unsigned int pos = 0; pos < this->objects.size(); pos++)
    {
      Entry temp;
      temp.obj = this->objects[pos];
      hierarchy.entries.push_back(temp);
    }

    hierarchy.Reorder_Entries();
    hierarchy.Build_Tree();
}
