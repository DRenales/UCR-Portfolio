#include "light.h"
#include "phong_shader.h"
#include "ray.h"
#include "render_world.h"
#include "object.h"

vec3 Phong_Shader::
Shade_Surface(const Ray& ray,const vec3& intersection_point,
    const vec3& normal,int recursion_depth) const
{
  //TODO; //determine the color
  // L_ambient + summation(L_world)
    vec3 color;

    color += world.ambient_color * world.ambient_intensity * this->color_ambient;

    for(auto light : world.lights)
    {
      vec3 intersect = light->position - intersection_point;
	  
	  if(world.enable_shadows) //THERE ARE SHADOWS SPOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOPYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYY
	  {
	    Ray shadow(intersection_point, intersect.normalized());
		Hit hit = world.Closest_Intersection(shadow);
		double shadow_magnitude = intersect.magnitude();

		if(hit.object != nullptr && shadow_magnitude > (shadow.direction * hit.dist).magnitude()) continue;
	  }
      
      vec3 intensity = light->Emitted_Light(intersect);

        //diffuse color
      double max_diffuse = std::max( dot(intersect.normalized(), normal), 0.0);
      color += this->color_diffuse * max_diffuse * intensity;

        //specular color
      double max_specular = std::max(0.0, dot( intersect.normalized(), normal));
      vec3 reflection = (2 * max_specular * normal) - intersect.normalized();

      double temp = (-1) * dot(ray.direction, reflection);
      if(  temp > 0 ) color += this->color_specular * intensity * std::pow(temp, this->specular_power);   

    }

    return color;
}
